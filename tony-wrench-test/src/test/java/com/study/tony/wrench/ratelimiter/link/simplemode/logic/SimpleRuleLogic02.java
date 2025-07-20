package com.study.tony.wrench.ratelimiter.link.simplemode.logic;

import com.study.tony.wrench.ratelimiter.link.simplemode.AbstractLogicChainNode;
import com.study.tony.wrench.ratelimiter.link.simplemode.factory.SimpleTradeRuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SimpleRuleLogic02 extends AbstractLogicChainNode<String, SimpleTradeRuleFactory.DynamicContext, String> {

    @Override
    public String apply(String requestParameter, SimpleTradeRuleFactory.DynamicContext dynamicContext) throws Exception {

        log.info("apply SimpleRuleLogic02 business logic...");

        return "chain of responsibility 单实例链";
    }

}
