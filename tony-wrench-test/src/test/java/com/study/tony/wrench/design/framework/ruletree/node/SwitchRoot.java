package com.study.tony.wrench.design.framework.ruletree.node;

import com.study.tony.wrench.design.framework.ruletree.IStrategyHandler;
import com.study.tony.wrench.design.framework.ruletree.factory.AbstractBusinessXxxSupport;
import com.study.tony.wrench.design.framework.ruletree.factory.DefaultStrategyFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SwitchRoot extends AbstractBusinessXxxSupport {

    private AccountNode accountNode;

    @Override
    protected String applyBusinessLogic(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("【switchNode】规则决策树 userId:{}", requestParameter);
        return route(requestParameter, dynamicContext);
    }

    @Override
    public IStrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> getStrategyHandler(String inputParams, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return accountNode;
    }

}
