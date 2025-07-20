package com.study.tony.wrench.ratelimiter.listener;

import com.study.tony.wrench.ratelimiter.domain.model.valobj.AttributeVO;
import com.study.tony.wrench.ratelimiter.domain.service.IDynamicConfigCenterService;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态配置中心调整监听器
 * 
 * 这个类实现了Redisson的MessageListener接口，用于监听Redis主题消息
 * 当配置发生变更时，Redis会发布消息到这个主题，监听器会接收到消息并处理
 * 
 * 工作流程：
 * 1. 应用启动时，在DynamicConfigCenterRegisterAutoConfig中被创建
 * 2. 绑定到Redis主题，监听AttributeVO类型的消息
 * 3. 当有配置变更时，Redis发布消息到主题
 * 4. 监听器接收到消息，调用dynamicConfigCenterService.adjustAttributeValue()方法
 * 5. 服务方法会更新对应的Bean字段值
 * 
 * 消息格式：
 * - 消息类型：AttributeVO
 * - 包含属性名和新值
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 */
public class DynamicConfigCenterAdjustListener implements MessageListener<AttributeVO> {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAdjustListener.class);

    /**
     * 动态配置中心服务
     * 用于处理配置变更的具体逻辑
     */
    private final IDynamicConfigCenterService dynamicConfigCenterService;

    /**
     * 构造函数
     * 
     * @param dynamicConfigCenterService 动态配置中心服务
     */
    public DynamicConfigCenterAdjustListener(IDynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    /**
     * 消息处理方法
     * 
     * 当Redis主题接收到AttributeVO类型的消息时，这个方法会被调用
     * 主要工作：
     * 1. 记录接收到的配置变更信息
     * 2. 调用动态配置中心服务处理配置变更
     * 3. 异常处理和日志记录
     * 
     * @param charSequence 主题名称
     * @param attributeVO 配置变更消息，包含属性名和新值
     */
    @Override
    public void onMessage(CharSequence charSequence, AttributeVO attributeVO) {
        try {
            // 记录接收到的配置变更信息
            log.info("xfg-wrench dcc config attribute:{} value:{}", attributeVO.getAttribute(), attributeVO.getValue());
            
            // 调用动态配置中心服务处理配置变更
            // 这个方法会：
            // 1. 更新Redis中的配置值
            // 2. 找到对应的Bean实例
            // 3. 通过反射更新Bean字段值
            dynamicConfigCenterService.updateAttribute(attributeVO);
        } catch (Exception e) {
            // 异常处理和日志记录
            log.error("xfg-wrench dcc config attribute:{} value:{}", attributeVO.getAttribute(), attributeVO.getValue(), e);
        }
    }

}
