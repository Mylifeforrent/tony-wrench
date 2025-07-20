package com.study.tony.wrench.ratelimiter.ruletree.node;

import com.alibaba.fastjson.JSON;
import com.study.tony.wrench.ratelimiter.ruletree.IStrategyHandler;
import com.study.tony.wrench.ratelimiter.ruletree.factory.AbstractBusinessXxxSupport;
import com.study.tony.wrench.ratelimiter.ruletree.factory.DefaultStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MemberLevel1Node extends AbstractBusinessXxxSupport {

    @Override
    protected String applyBusinessLogic(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("【级别节点-1】规则决策树 userId:{}",requestParameter);
        log.info("【级别节点-1】动态上下文:{}", JSON.toJSONString(dynamicContext));
        return "level1" + JSON.toJSONString(dynamicContext);
    }

    @Override
    public IStrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> getStrategyHandler(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return IStrategyHandler.DEFAULT;
    }
}
