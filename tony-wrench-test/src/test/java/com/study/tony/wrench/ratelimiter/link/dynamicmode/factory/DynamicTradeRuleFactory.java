package com.study.tony.wrench.ratelimiter.link.dynamicmode.factory;

import com.study.tony.wrench.ratelimiter.link.dynamicmode.LogicChainArmory;
import com.study.tony.wrench.ratelimiter.link.dynamicmode.chain.BusinessLogicChain;
import com.study.tony.wrench.ratelimiter.link.dynamicmode.logic.DynamicRuleLogic01;
import com.study.tony.wrench.ratelimiter.link.dynamicmode.logic.DynamicRuleLogic02;
import com.study.tony.wrench.ratelimiter.link.dynamicmode.logic.XxxResponse;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description
 * @create 2025-01-18 09:19
 */
@Service
public class DynamicTradeRuleFactory {

    @Bean("demo01")
    public BusinessLogicChain<String, DynamicContext, XxxResponse> demo01(DynamicRuleLogic01 ruleLogic201, DynamicRuleLogic02 ruleLogic202) {

        LogicChainArmory<String, DynamicContext, XxxResponse> logicChainArmory = new LogicChainArmory<>("demo01", ruleLogic201, ruleLogic202);

        return logicChainArmory.getLogicChain();
    }

    @Bean("demo02")
    public BusinessLogicChain<String, DynamicContext, XxxResponse> demo02(DynamicRuleLogic02 ruleLogic202) {

        LogicChainArmory<String, DynamicContext, XxxResponse> logicChainArmory = new LogicChainArmory<>("demo02", ruleLogic202);

        return logicChainArmory.getLogicChain();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext extends com.study.tony.wrench.ratelimiter.link.dynamicmode.DynamicContext {
        private String age;
    }

}
