package com.study.tony.wrench.design.framework.link.simplemode;


public interface ILogicChainNode<I, D, R> extends ILogicChainArmory<I, D, R> {

    R apply(I requestParameter, D dynamicContext) throws Exception;

}
