package com.study.tony.wrench.configcenter.config;

import com.study.tony.wrench.configcenter.domain.service.IDynamicConfigCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * 动态配置中心自动配置类
 * 
 * 这个类实现了BeanPostProcessor接口，是Spring Bean生命周期的重要参与者
 * 
 * 工作时机：
 * 1. Spring容器启动时，所有Bean都会被这个后处理器处理
 * 2. 在Bean初始化完成后（postProcessAfterInitialization），会扫描Bean中的@DCCValue注解
 * 3. 如果发现带有@DCCValue注解的字段，会从Redis读取配置值并注入
 * 
 * 执行顺序：
 * 1. 在DynamicConfigCenterRegisterAutoConfig创建完所有Bean之后
 * 2. Spring开始创建应用中的其他Bean
 * 3. 每个Bean创建完成后都会经过这个后处理器
 * 4. 后处理器会调用dynamicConfigCenterService.proxyObject()方法处理Bean
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
@Slf4j
@Configuration
public class DynamicConfigCenterAutoConfig implements BeanPostProcessor {

    /**
     * 动态配置中心服务
     * 通过构造函数注入，在Bean后处理时使用
     */
    private final IDynamicConfigCenterService dynamicConfigCenterService;

    /**
     * 构造函数，Spring会自动注入IDynamicConfigCenterService
     * 
     * @param dynamicConfigCenterService 动态配置中心服务
     */
    public DynamicConfigCenterAutoConfig(IDynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    /**
     * Bean初始化后的处理
     * 
     * 这是BeanPostProcessor的核心方法，会在每个Bean初始化完成后被调用
     * 在这里会：
     * 1. 扫描Bean中带有@DCCValue注解的字段
     * 2. 从Redis读取对应的配置值
     * 3. 将配置值注入到Bean的字段中
     * 4. 将Bean注册到配置中心的管理中，以便后续动态更新
     * 
     * @param bean 初始化完成的Bean实例
     * @param beanName Bean的名称
     * @return 处理后的Bean实例（通常是原Bean，但字段值可能被修改）
     * @throws BeansException 如果处理过程中发生异常
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 调用动态配置中心服务处理Bean
        // 这个方法会扫描Bean中的@DCCValue注解，并从Redis读取配置值
        log.info("proxy bean name ==> {}", beanName);
        return dynamicConfigCenterService.initAttributeByProxy(bean);
    }

}
