package com.study.tony.wrench.ratelimiter.types.common;

/**
 * 动态配置中心常量类
 * 
 * 定义了动态配置中心中使用的各种常量，包括：
 * 1. Redis主题名称前缀
 * 2. 配置值分隔符
 * 3. 主题名称生成方法
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
public class Constants {

    /**
     * 动态配置中心Redis主题名称前缀
     * 
     * 用于生成Redis主题名称，格式：DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_系统名
     * 例如：如果系统名为"user-service"，则主题名为"DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_user-service"
     * 
     * 这个主题用于：
     * 1. 发布配置变更消息
     * 2. 订阅配置变更通知
     * 3. 实现配置的实时同步
     */
    public final static String DYNAMIC_CONFIG_CENTER_REDIS_TOPIC = "DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_";

    /**
     * 配置值分隔符
     * 
     * 用于分隔@DCCValue注解中的属性名和默认值
     * 格式：属性名:默认值
     * 例如：@DCCValue("isSwitch:true") 中，":"就是分隔符
     */
    public final static String SYMBOL_COLON = ":";

    /**
     * 生成Redis主题名称
     * 
     * 根据系统名称生成完整的Redis主题名称
     * 格式：DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_系统名
     * 
     * @param application 系统名称，通常来自配置文件中的xfg.wrench.config.system属性
     * @return 完整的Redis主题名称
     */
    public static String getTopic(String application) {
        return DYNAMIC_CONFIG_CENTER_REDIS_TOPIC + application;
    }

}
