package com.study.tony.wrench.configcenter.types.annotations;

import java.lang.annotation.*;

/**
 * 动态配置中心注解
 * 
 * 用于标记需要动态配置的字段，支持运行时动态更新
 * 
 * 使用方法：
 * 1. 在需要动态配置的字段上添加@DCCValue注解
 * 2. 注解值格式：属性名:默认值
 * 3. 例如：@DCCValue("isSwitch:true") 表示属性名为isSwitch，默认值为true
 * 
 * 工作流程：
 * 1. 应用启动时，BeanPostProcessor会扫描带有此注解的字段
 * 2. 从Redis读取配置值，如果不存在则使用默认值
 * 3. 将配置值注入到字段中
 * 4. 运行时，通过Redis主题监听配置变更，实时更新字段值
 * 
 * 示例：
 * ```java
 * @Component
 * public class MyService {
 *     @DCCValue("maxThreads:10")
 *     private String maxThreads;
 *     
 *     @DCCValue("timeout:5000")
 *     private String timeout;
 * }
 * ```
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025年04月19日09:51:38
 */
@Retention(RetentionPolicy.RUNTIME)  // 运行时保留，可以通过反射获取
@Target({ElementType.FIELD, ElementType.METHOD})  // 可以标记在字段和方法上
@Documented  // 生成文档
public @interface DCCValue {

    /**
     * 配置值，格式：属性名:默认值
     * 
     * 例如：
     * - "isSwitch:true" - 属性名为isSwitch，默认值为true
     * - "maxThreads:10" - 属性名为maxThreads，默认值为10
     * - "timeout:5000" - 属性名为timeout，默认值为5000
     * 
     * @return 配置值字符串
     */
    String value() default "";

}
