package com.study.tony.wrench.design.framework.link.dynamicmode.handler;

import com.study.tony.wrench.design.framework.link.dynamicmode.DynamicContext;

public interface ILogicHandler<I, D extends DynamicContext, R> {

    default R next(I requestParameter, D dynamicContext) {
        dynamicContext.setProceed(true);
        return null;
    }

    default R stop(I requestParameter, D dynamicContext, R result){
        dynamicContext.setProceed(false);
        return result;
    }

    R apply(I requestParameter, D dynamicContext) throws Exception;

}
