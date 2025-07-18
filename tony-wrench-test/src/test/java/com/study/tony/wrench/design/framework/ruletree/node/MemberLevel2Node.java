package com.study.tony.wrench.design.framework.ruletree.node;

import com.alibaba.fastjson.JSON;
import com.study.tony.wrench.design.framework.ruletree.IStrategyHandler;
import com.study.tony.wrench.design.framework.ruletree.factory.AbstractBusinessXxxSupport;
import com.study.tony.wrench.design.framework.ruletree.factory.DefaultStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MemberLevel2Node extends AbstractBusinessXxxSupport {

    @Override
    protected String applyBusinessLogic(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("【级别节点-2】规则决策树 userId:{}", requestParameter);
        log.info("【级别节点-2】动态上下文:{}", JSON.toJSONString(dynamicContext));
        return "level2" + JSON.toJSONString(dynamicContext);
    }

    @Override
    public IStrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> getStrategyHandler(String inputParams, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return IStrategyHandler.DEFAULT;
    }

}
