package com.study.tony.wrench.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态配置中心自动配置属性类
 * 
 * 用于绑定配置文件中的动态配置中心相关属性
 * 配置前缀：tony.wrench.config
 * 
 * 配置示例：
 * ```yaml
 * tony:
 *   wrench:
 *     config:
 *       system: user-service  # 系统名称，用于生成Redis键和主题名
 *       enabled: true         # 是否启用动态配置中心
 *       refresh-interval: 5000 # 配置刷新间隔（毫秒）
 * ```
 * 
 * 主要功能：
 * 1. 定义系统名称，用于区分不同系统的配置
 * 2. 提供Redis键生成方法，格式：系统名_属性名
 * 3. 支持配置开关和刷新间隔设置
 * 
 * @author Tony
 */
@ConfigurationProperties(prefix = "tony.wrench.config", ignoreInvalidFields = true)
public class DynamicConfigCenterAutoProperties {

    /**
     * 系统名称
     * 
     * 用于标识当前应用系统，主要用途：
     * 1. 生成Redis键前缀，避免不同系统间的配置冲突
     * 2. 生成Redis主题名称，实现系统级别的配置隔离
     * 3. 在日志中标识配置来源
     * 
     * 配置示例：
     * - user-service：用户服务
     * - order-service：订单服务
     * - payment-service：支付服务
     * 
     * 默认值：default-system
     */
    private String system = "default-system";

    /**
     * 是否启用动态配置中心
     * 
     * 控制是否启用动态配置功能
     * - true：启用动态配置，会监听Redis配置变更
     * - false：禁用动态配置，使用静态配置
     * 
     * 默认值：true
     */
    private boolean enabled = true;

    /**
     * 配置刷新间隔（毫秒）
     * 
     * 定时刷新配置的时间间隔
     * 建议值：1000-10000毫秒
     * 
     * 默认值：5000毫秒
     */
    private long refreshInterval = 5000;

    /**
     * Redis键前缀
     * 
     * 用于生成Redis键的前缀
     * 格式：系统名_属性名
     * 
     * 默认值：tony_wrench_config
     */
    private String keyPrefix = "tony_wrench_config";

    /**
     * 生成Redis键名
     * 
     * 根据属性名生成完整的Redis键名
     * 格式：系统名_属性名
     * 
     * 例如：
     * - 系统名：user-service，属性名：isSwitch → user-service_isSwitch
     * - 系统名：order-service，属性名：maxThreads → order-service_maxThreads
     * 
     * @param attributeName 属性名称
     * @return 完整的Redis键名
     */
    public String getKey(String attributeName) {
        return this.system + "_" + attributeName;
    }

    /**
     * 生成带前缀的Redis键名
     * 
     * 根据属性名生成带前缀的完整Redis键名
     * 格式：前缀_系统名_属性名
     * 
     * @param attributeName 属性名称
     * @return 带前缀的完整Redis键名
     */
    public String getKeyWithPrefix(String attributeName) {
        return this.keyPrefix + "_" + this.system + "_" + attributeName;
    }

    // Getter and Setter methods

    /**
     * 获取系统名称
     * 
     * @return 系统名称
     */
    public String getSystem() {
        return system;
    }

    /**
     * 设置系统名称
     * 
     * @param system 系统名称
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * 是否启用动态配置中心
     * 
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用动态配置中心
     * 
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取配置刷新间隔
     * 
     * @return 刷新间隔（毫秒）
     */
    public long getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * 设置配置刷新间隔
     * 
     * @param refreshInterval 刷新间隔（毫秒）
     */
    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     * 获取Redis键前缀
     * 
     * @return Redis键前缀
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * 设置Redis键前缀
     * 
     * @param keyPrefix Redis键前缀
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
