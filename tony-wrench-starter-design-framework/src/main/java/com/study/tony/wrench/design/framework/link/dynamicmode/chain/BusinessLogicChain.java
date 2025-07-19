package com.study.tony.wrench.design.framework.link.dynamicmode.chain;


import com.study.tony.wrench.design.framework.link.dynamicmode.DynamicContext;
import com.study.tony.wrench.design.framework.link.dynamicmode.handler.ILogicHandler;

public class BusinessLogicChain<I, D extends DynamicContext, R> extends LinkedList<ILogicHandler<I, D, R>> implements ILogicHandler<I, D, R>{

    public BusinessLogicChain(String name) {
        super(name);
    }

    @Override
    public R apply(I requestParameter, D dynamicContext) throws Exception {
        Node<ILogicHandler<I, D, R>> current = this.first;
        do {
            ILogicHandler<I, D, R> item = current.item;
            R result = item.apply(requestParameter, dynamicContext);
            if (!dynamicContext.isProceed()) return result;

            current = current.next;
        } while (null != current);

        return null;
    }

}
