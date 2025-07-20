package com.study.tony.wrench.ratelimiter.ruletree;

@FunctionalInterface
public interface IStrategyHandler<I, D, R> {

    /**
     * 受理规则处理
     *
     * @param inputParams 入参
     * @param dynamicContext 上下文参数
     * @return 返参
     * @throws Exception 异常
     */
    R apply(I inputParams, D dynamicContext) throws Exception;

    /**
     * 默认的规则处理器
     */
    IStrategyHandler DEFAULT = (I, D) -> null;
}
