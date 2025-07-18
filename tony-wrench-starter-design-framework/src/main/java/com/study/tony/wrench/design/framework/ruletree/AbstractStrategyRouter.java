package com.study.tony.wrench.design.framework.ruletree;

public abstract class AbstractStrategyRouter<I, D, R> implements IStrategyHandlerMapper<I, D, R>, IStrategyHandler<I, D, R> {

    IStrategyHandler<I, D, R> DEFAULT = IStrategyHandler.DEFAULT;

    public R route(I requestParameter, D dynamicContext) throws Exception {
        IStrategyHandler<I, D, R> strategyHandler = getStrategyHandler(requestParameter, dynamicContext);
        if (null != strategyHandler) return strategyHandler.apply(requestParameter, dynamicContext);
        return DEFAULT.apply(requestParameter, dynamicContext);
        //maybe we can directly return null
//        return  null;
    }

}
