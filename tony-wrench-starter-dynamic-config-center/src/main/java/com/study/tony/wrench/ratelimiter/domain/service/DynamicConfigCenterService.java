package com.study.tony.wrench.ratelimiter.domain.service;

import com.study.tony.wrench.ratelimiter.config.DynamicConfigCenterAutoConfig;
import com.study.tony.wrench.ratelimiter.config.properties.DynamicConfigCenterAutoProperties;
import com.study.tony.wrench.ratelimiter.domain.model.valobj.AttributeVO;
import com.study.tony.wrench.ratelimiter.types.annotations.DCCValue;
import com.study.tony.wrench.ratelimiter.types.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态配置中心服务实现类
 * <p>
 * 这是整个动态配置中心的核心服务类，负责：
 * 1. 扫描Bean中的@DCCValue注解字段
 * 2. 从Redis读取配置值并注入到Bean中
 * 3. 管理Bean与配置的映射关系
 * 4. 处理配置变更的动态更新
 * <p>
 * 工作流程：
 * 1. 应用启动时，通过BeanPostProcessor调用proxyObject方法
 * 2. 扫描Bean中带有@DCCValue注解的字段
 * 3. 从Redis读取配置值，如果不存在则使用默认值并写入Redis
 * 4. 将配置值注入到Bean字段中
 * 5. 将Bean注册到dccBeanGroup中，以便后续动态更新
 * 6. 运行时，通过Redis主题监听配置变更，实时更新Bean字段值
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
public class DynamicConfigCenterService implements IDynamicConfigCenterService {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAutoConfig.class);

    /**
     * 动态配置属性，包含系统名称等配置信息
     */
    private final DynamicConfigCenterAutoProperties properties;

    /**
     * Redis客户端，用于读取和写入配置值
     */
    private final RedissonClient redissonClient;

    /**
     * Bean与配置的映射关系
     * Key: Redis配置键（格式：系统名_属性名）
     * Value: 对应的Bean实例
     * 用于配置变更时快速找到需要更新的Bean
     */
    private final Map<String, Object> dccBeanGroup = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param properties     动态配置属性
     * @param redissonClient Redis客户端
     */
    public DynamicConfigCenterService(DynamicConfigCenterAutoProperties properties, RedissonClient redissonClient) {
        this.properties = properties;
        this.redissonClient = redissonClient;
    }

    /**
     * 处理Bean对象，扫描并注入动态配置
     * <p>
     * 这个方法在Bean初始化完成后被调用，主要工作：
     * 1. 处理AOP代理对象，获取真实的目标类
     * 2. 扫描类中带有@DCCValue注解的字段
     * 3. 解析注解值，格式：属性名:默认值
     * 4. 从Redis读取配置值，如果不存在则使用默认值
     * 5. 将配置值注入到Bean字段中
     * 6. 将Bean注册到管理映射中
     *
     * @param bean 需要处理的Bean实例
     * @return 处理后的Bean实例
     */
    @Override
    public Object initAttributeByProxy(Object bean) {
        // 处理AOP代理对象
        // 注意：增加AOP代理后，获得类的方式要通过AopProxyUtils.getTargetClass(bean)
        // 不能直接bean.class，因为代理后类的结构发生变化，这样不能获得到自己的自定义注解了
        Class<?> targetBeanClass = bean.getClass();
        Object targetBeanObject = bean;
        if (AopUtils.isAopProxy(bean)) {
            targetBeanClass = AopUtils.getTargetClass(bean);
            targetBeanObject = AopProxyUtils.getSingletonTarget(bean);
        }

        // 获取类中声明的所有字段
        Field[] fields = targetBeanClass.getDeclaredFields();
        for (Field field : fields) {
            // 检查字段是否带有@DCCValue注解
            if (!field.isAnnotationPresent(DCCValue.class)) {
                continue;
            }

            // 获取注解信息
            DCCValue dccValue = field.getAnnotation(DCCValue.class);
            String value = dccValue.value();

            // 验证注解值格式
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException(field.getName() + " @DCCValue is not config value config case 「isSwitch/isSwitch:1」");
            }

            // 解析注解值，格式：属性名:默认值
            String[] splits = value.split(Constants.SYMBOL_COLON);
            String key = properties.getKey(splits[0].trim());  // 生成Redis键：系统名_属性名
            String defaultValue = splits.length == 2 ? splits[1] : null;

            // 设置值，默认为默认值
            String setValue = defaultValue;

            try {
                // 验证默认值不能为空
                if (StringUtils.isBlank(defaultValue)) {
                    throw new RuntimeException("dcc config error " + key + " is not null - 请配置默认值！");
                }

                // Redis操作：判断配置Key是否存在，不存在则创建，存在则获取最新值
                RBucket<String> bucket = redissonClient.getBucket(key);
                boolean exists = bucket.isExists();
                if (!exists) {
                    // 如果Redis中不存在该配置，则使用默认值并写入Redis
                    bucket.set(defaultValue);
                } else {
                    // 如果Redis中存在该配置，则读取最新值
                    setValue = bucket.get();
                }

                // 通过反射将配置值注入到Bean字段中
                field.setAccessible(true);
                field.set(targetBeanObject, setValue);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 将Bean注册到管理映射中，以便后续动态更新
            dccBeanGroup.put(key, targetBeanObject);
        }

        return bean;
    }

    /**
     * 动态调整属性值
     * <p>
     * 这个方法在配置变更时被调用，主要工作：
     * 1. 根据属性信息生成Redis键
     * 2. 更新Redis中的配置值
     * 3. 找到对应的Bean实例
     * 4. 通过反射更新Bean字段值
     *
     * @param attributeVO 属性值对象，包含属性名和新值
     */
    @Override
    public void updateAttribute(AttributeVO attributeVO) {
        // 根据属性信息生成Redis键
        String key = properties.getKey(attributeVO.getAttribute());
        String value = attributeVO.getValue();

        // 更新Redis中的配置值
        RBucket<String> bucket = redissonClient.getBucket(key);
        boolean exists = bucket.isExists();
        if (!exists) return;  // 如果配置不存在，直接返回
        bucket.set(attributeVO.getValue());

        // 从管理映射中获取对应的Bean实例
        Object objBean = dccBeanGroup.get(key);
        if (null == objBean) return;

        // 处理AOP代理对象，获取真实的目标类
        Class<?> objBeanClass = objBean.getClass();
        if (AopUtils.isAopProxy(objBean)) {
            objBeanClass = AopUtils.getTargetClass(objBean);
        }
        log.info("key need to be updated: {}, value: {}, bean: {}", key, value, objBean.getClass().getName());

        try {
            // 通过反射获取字段并更新值
            // getDeclaredField方法用于获取指定类中声明的所有字段，包括私有字段、受保护字段和公共字段
            Field field = objBeanClass.getDeclaredField(attributeVO.getAttribute());
            field.setAccessible(true);
            field.set(objBean, value);
            field.setAccessible(false);

            log.info("DCC 节点监听，动态设置值 key: {} value: {}, class: {}", key, value, objBean.getClass().getName());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
