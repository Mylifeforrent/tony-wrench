package com.study.tony.wrench.design.framework.link.simplemode;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽象类
 * @create 2025-01-18 09:14
 */
public abstract class AbstractLogicChainNode<I, D, R> implements ILogicChainNode<I, D, R> {

    private ILogicChainNode<I, D, R> next;

    @Override
    public ILogicChainNode<I, D, R> next() {
        return next;
    }

    @Override
    public ILogicChainNode<I, D, R> appendNext(ILogicChainNode<I, D, R> next) {
        this.next = next;
        return next;
    }

    protected R next(I requestParameter, D dynamicContext) throws Exception {
        return next.apply(requestParameter, dynamicContext);
    }
}
