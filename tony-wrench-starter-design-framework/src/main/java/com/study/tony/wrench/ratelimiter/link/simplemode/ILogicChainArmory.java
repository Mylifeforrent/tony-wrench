package com.study.tony.wrench.ratelimiter.link.simplemode;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 责任链装配
 * @create 2025-01-18 09:10
 */
public interface ILogicChainArmory<I, D, R> {

    ILogicChainNode<I, D, R> next();

    ILogicChainNode<I, D, R> appendNext(ILogicChainNode<I, D, R> next);

}
