package com.study.tony.wrench.config;
import com.study.tony.wrench.config.properties.DynamicConfigCenterAutoProperties;
import com.study.tony.wrench.config.properties.DynamicConfigCenterRegisterAutoProperties;
import com.study.tony.wrench.domain.model.valobj.AttributeVO;
import com.study.tony.wrench.domain.service.DynamicConfigCenterService;
import com.study.tony.wrench.domain.service.IDynamicConfigCenterService;
import com.study.tony.wrench.listener.DynamicConfigCenterAdjustListener;
import com.study.tony.wrench.types.common.Constants;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态配置中心注册自动配置类
 *
 * 这是整个动态配置中心的核心配置类，负责：
 * 1. 启用配置属性绑定
 * 2. 创建Redis连接客户端
 * 3. 注册动态配置服务
 * 4. 注册配置变更监听器
 * 5. 创建Redis主题订阅
 *
 * Spring Boot启动时会按照以下顺序创建Bean：
 * 1. redissonClient - Redis客户端连接
 * 2. dynamicConfigCenterService - 动态配置服务实现
 * 3. dynamicConfigCenterAdjustListener - 配置变更监听器
 * 4. dynamicConfigCenterRedisTopic - Redis主题订阅
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
@Configuration
@EnableConfigurationProperties(value = {
        DynamicConfigCenterAutoProperties.class,    // 启用动态配置属性绑定
        DynamicConfigCenterRegisterAutoProperties.class})  // 启用注册配置属性绑定
public class DynamicConfigCenterRegisterAutoConfig {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterRegisterAutoConfig.class);

    /**
     * 创建Redis客户端连接
     *
     * 这是第一个被创建的Bean，为后续的配置服务提供Redis连接支持
     * 使用Redisson作为Redis客户端，支持分布式锁、主题订阅等高级功能
     *
     * @param properties 注册配置属性，包含Redis连接信息
     * @return RedissonClient Redis客户端实例
     */
    @Bean("xfgWrenchRedissonClient")
    public RedissonClient redissonClient(DynamicConfigCenterRegisterAutoProperties properties) {
        Config config = new Config();
        // 设置JSON编解码器，用于序列化/反序列化消息
        config.setCodec(JsonJacksonCodec.INSTANCE);

        // 配置单机Redis连接
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setConnectionPoolSize(properties.getPoolSize())           // 连接池大小
                .setConnectionMinimumIdleSize(properties.getMinIdleSize()) // 最小空闲连接数
                .setIdleConnectionTimeout(properties.getIdleTimeout())     // 空闲连接超时时间
                .setConnectTimeout(properties.getConnectTimeout())         // 连接超时时间
                .setRetryAttempts(properties.getRetryAttempts())           // 重试次数
                .setRetryInterval(properties.getRetryInterval())           // 重试间隔
                .setPingConnectionInterval(properties.getPingInterval())   // 心跳检测间隔
                .setKeepAlive(properties.isKeepAlive())                   // 是否保持长连接
        ;

        // 只有在密码不为空时才设置密码
        if (properties.getPassword() != null && !properties.getPassword().trim().isEmpty()) {
            config.useSingleServer().setPassword(properties.getPassword());
        }
        ;

        RedissonClient redissonClient = Redisson.create(config);

        log.info("xfg-wrench，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    /**
     * 创建动态配置中心服务
     *
     * 这是第二个被创建的Bean，负责：
     * 1. 扫描带有@DCCValue注解的字段
     * 2. 从Redis读取配置值并注入到Bean中
     * 3. 管理配置变更的动态更新
     *
     * @param dynamicConfigCenterAutoProperties 动态配置属性
     * @param xfgWrenchRedissonClient Redis客户端
     * @return IDynamicConfigCenterService 动态配置服务接口
     */
    @Bean
    public IDynamicConfigCenterService dynamicConfigCenterService(DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties, RedissonClient xfgWrenchRedissonClient) {
        return new DynamicConfigCenterService(dynamicConfigCenterAutoProperties, xfgWrenchRedissonClient);
    }

    /**
     * 创建配置变更监听器
     *
     * 这是第三个被创建的Bean，负责监听Redis主题消息
     * 当配置发生变更时，会接收到消息并调用服务进行动态更新
     *
     * @param dynamicConfigCenterService 动态配置服务
     * @return DynamicConfigCenterAdjustListener 配置变更监听器
     */
    @Bean
    public DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener(IDynamicConfigCenterService dynamicConfigCenterService) {
        return new DynamicConfigCenterAdjustListener(dynamicConfigCenterService);
    }

    /**
     * 创建Redis主题订阅
     *
     * 这是最后一个被创建的Bean，负责：
     * 1. 创建Redis主题订阅
     * 2. 将监听器绑定到主题
     * 3. 实现配置变更的实时通知机制
     *
     * @param dynamicConfigCenterAutoProperties 动态配置属性
     * @param redissonClient Redis客户端
     * @param dynamicConfigCenterAdjustListener 配置变更监听器
     * @return RTopic Redis主题实例
     */
    @Bean(name = "dynamicConfigCenterRedisTopic")
    public RTopic threadPoolConfigAdjustListener(DynamicConfigCenterAutoProperties dynamicConfigCenterAutoProperties,
                                                 RedissonClient redissonClient,
                                                 DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener) {
        // 根据系统名称创建主题名称，格式：DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_系统名
        RTopic topic = redissonClient.getTopic(Constants.getTopic(dynamicConfigCenterAutoProperties.getSystem()));
        // 将监听器绑定到主题，当有AttributeVO类型的消息时，会触发监听器
        topic.addListener(AttributeVO.class, dynamicConfigCenterAdjustListener);
        return topic;
    }

}
