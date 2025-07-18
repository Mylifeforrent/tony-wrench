package com.study.tony.wrench.design.framework.ruletree;

@FunctionalInterface
public interface IStrategyHandlerMapper<I, D, R> {

    IStrategyHandler<I, D, R> getStrategyHandler(I inputParams, D dynamicContext) throws Exception;

}
