package com.study.tony.wrench.configcenter.domain.model.valobj;

/**
 * 属性值调整值对象
 * 
 * 用于在Redis主题中传递配置变更消息的值对象
 * 当配置发生变更时，会创建这个对象并通过Redis主题发布消息
 * 
 * 使用场景：
 * 1. 配置变更时，创建AttributeVO对象
 * 2. 通过Redis主题发布消息
 * 3. 监听器接收到消息后，使用AttributeVO中的信息更新Bean字段
 * 
 * 消息流程：
 * 1. 外部系统修改配置 → 创建AttributeVO对象
 * 2. 发布到Redis主题 → 监听器接收消息
 * 3. 调用adjustAttributeValue方法 → 更新Bean字段值
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
public class AttributeVO {

    /**
     * 属性名称
     * 
     * 对应@DCCValue注解中配置的属性名
     * 例如：@DCCValue("isSwitch:true") 中的"isSwitch"
     * 
     * 这个字段用于：
     * 1. 标识需要更新的配置项
     * 2. 在Bean中查找对应的字段
     * 3. 生成Redis键名
     */
    private String attribute;

    /**
     * 新的配置值
     * 
     * 配置变更后的新值，会替换原有的配置值
     * 例如："true"、"false"、"10"、"5000"等
     * 
     * 这个字段用于：
     * 1. 更新Redis中的配置值
     * 2. 通过反射设置Bean字段的新值
     */
    private String value;

    /**
     * 默认构造函数
     * 用于JSON反序列化
     */
    public AttributeVO() {
    }

    /**
     * 带参数的构造函数
     * 
     * @param attribute 属性名称
     * @param value 新的配置值
     */
    public AttributeVO(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    /**
     * 获取属性名称
     * 
     * @return 属性名称
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * 设置属性名称
     * 
     * @param attribute 属性名称
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * 获取配置值
     * 
     * @return 配置值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置配置值
     * 
     * @param value 配置值
     */
    public void setValue(String value) {
        this.value = value;
    }
}
