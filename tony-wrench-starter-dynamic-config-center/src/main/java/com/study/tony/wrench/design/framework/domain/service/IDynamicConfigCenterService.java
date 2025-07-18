package com.study.tony.wrench.design.framework.domain.service;


import com.study.tony.wrench.design.framework.domain.model.valobj.AttributeVO;

/**
 * 动态配置中心服务接口
 * 
 * 定义了动态配置中心的核心服务方法，包括：
 * 1. Bean代理处理 - 在应用启动时扫描和注入配置
 * 2. 属性值调整 - 在运行时动态更新配置值
 * 
 * 实现类：DynamicConfigCenterService
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-04-19 09:54
 */
public interface IDynamicConfigCenterService {

    /**
     * 处理Bean对象，扫描并注入动态配置
     * 
     * 这个方法在Bean初始化完成后被调用，主要工作：
     * 1. 扫描Bean中带有@DCCValue注解的字段
     * 2. 从Redis读取配置值，如果不存在则使用默认值
     * 3. 将配置值注入到Bean字段中
     * 4. 将Bean注册到管理映射中，以便后续动态更新
     * 
     * 调用时机：
     * - 应用启动时，通过BeanPostProcessor调用
     * - 每个Bean初始化完成后都会调用一次
     * 
     * @param bean 需要处理的Bean实例
     * @return 处理后的Bean实例（通常是原Bean，但字段值可能被修改）
     */
    Object initAttributeByProxy(Object bean);

    /**
     * 动态调整属性值
     * 
     * 这个方法在配置变更时被调用，主要工作：
     * 1. 根据属性信息生成Redis键
     * 2. 更新Redis中的配置值
     * 3. 找到对应的Bean实例
     * 4. 通过反射更新Bean字段值
     * 
     * 调用时机：
     * - 运行时，通过Redis主题监听器调用
     * - 当有配置变更消息时触发
     * 
     * @param attributeVO 属性值对象，包含属性名和新值
     */
    void updateAttribute(AttributeVO attributeVO);

}
