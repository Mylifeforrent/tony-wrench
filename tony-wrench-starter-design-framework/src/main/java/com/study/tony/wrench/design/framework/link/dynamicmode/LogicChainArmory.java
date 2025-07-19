package com.study.tony.wrench.design.framework.link.dynamicmode;


import com.study.tony.wrench.design.framework.link.dynamicmode.chain.BusinessLogicChain;
import com.study.tony.wrench.design.framework.link.dynamicmode.handler.ILogicHandler;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 链路装配
 * @create 2025-01-18 10:02
 */
public class LogicChainArmory<I, D extends DynamicContext, R> {

    private final BusinessLogicChain<I, D, R> logicChain;

    @SafeVarargs
    public LogicChainArmory(String chainName, ILogicHandler<I, D, R>... logicHandlers) {
        logicChain = new BusinessLogicChain<>(chainName);
        for (ILogicHandler<I, D, R> logicHandler: logicHandlers){
            logicChain.add(logicHandler);
        }
    }

    public BusinessLogicChain<I, D, R> getLogicChain() {
        return logicChain;
    }

}
