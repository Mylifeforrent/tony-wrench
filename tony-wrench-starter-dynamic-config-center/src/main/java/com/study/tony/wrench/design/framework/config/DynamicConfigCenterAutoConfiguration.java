package com.study.tony.wrench.design.framework.config;

import com.study.tony.wrench.design.framework.config.properties.DynamicConfigCenterAutoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 动态配置中心自动配置类
 * 
 * 主要功能：
 * 1. 启用配置属性绑定
 * 2. 提供条件化自动配置
 * 3. 确保配置属性能够被IDEA识别
 * 
 * 配置条件：
 * - 当 tony.wrench.config.enabled=true 时启用（默认启用）
 * 
 * @author Tony
 */
@Configuration
@EnableConfigurationProperties(DynamicConfigCenterAutoProperties.class)
@ConditionalOnProperty(prefix = "tony.wrench.config", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicConfigCenterAutoConfiguration {

    /**
     * 构造函数
     * 
     * 用于验证配置是否正确加载
     */
    public DynamicConfigCenterAutoConfiguration(DynamicConfigCenterAutoProperties properties) {
        // 验证配置是否正确加载
        if (properties.getSystem() == null || properties.getSystem().trim().isEmpty()) {
            throw new IllegalArgumentException("tony.wrench.config.system 配置不能为空");
        }
    }
} 