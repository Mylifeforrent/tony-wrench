package com.study.tony.wrench.design.framework.ruletree;

public abstract class AbstractMultiThreadStrategyRouter<I, D, R> implements IStrategyHandlerMapper<I, D, R>, IStrategyHandler<I, D, R> {

    IStrategyHandler<I, D, R> DEFAULT = IStrategyHandler.DEFAULT;

    public R route(I requestParameter, D dynamicContext) throws Exception {
        IStrategyHandler<I, D, R> strategyHandler = getStrategyHandler(requestParameter, dynamicContext);
        if (null != strategyHandler) return strategyHandler.apply(requestParameter, dynamicContext);
        return DEFAULT.apply(requestParameter, dynamicContext);
        //maybe we can directly return null
//        return  null;
    }

    @Override
    public R apply(I inputParams, D dynamicContext) throws Exception {
        //load data by multiple thread
        prepareDataByMultiThread(inputParams, dynamicContext);
        //process business logic
        return applyBusinessLogic(inputParams, dynamicContext);
    }

    protected abstract void prepareDataByMultiThread(I requestParameter, D dynamicContext) throws Exception;

    protected abstract R applyBusinessLogic(I requestParameter, D dynamicContext) throws Exception;
}
