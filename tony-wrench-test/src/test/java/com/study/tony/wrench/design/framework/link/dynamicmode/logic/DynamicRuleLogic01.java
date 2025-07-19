package com.study.tony.wrench.design.framework.link.dynamicmode.logic;

import com.study.tony.wrench.design.framework.link.dynamicmode.factory.DynamicTradeRuleFactory;
import com.study.tony.wrench.design.framework.link.dynamicmode.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description
 * @create 2025-01-18 09:18
 */
@Slf4j
@Service
public class DynamicRuleLogic01 implements ILogicHandler<String, DynamicTradeRuleFactory.DynamicContext, XxxResponse> {

    public XxxResponse apply(String requestParameter, DynamicTradeRuleFactory.DynamicContext dynamicContext) throws Exception{

        log.info("link model02 RuleLogic201");

        return next(requestParameter, dynamicContext);
    }

}
