package com.study.tony.wrench.ratelimiter.link.simplemode;


public interface ILogicChainNode<I, D, R> extends ILogicChainArmory<I, D, R> {

    R apply(I requestParameter, D dynamicContext) throws Exception;

}
