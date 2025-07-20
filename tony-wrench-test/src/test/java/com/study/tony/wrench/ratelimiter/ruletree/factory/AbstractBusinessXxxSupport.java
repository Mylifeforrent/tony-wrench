package com.study.tony.wrench.ratelimiter.ruletree.factory;

import com.study.tony.wrench.ratelimiter.ruletree.AbstractMultiThreadStrategyRouter;

public abstract class AbstractBusinessXxxSupport extends AbstractMultiThreadStrategyRouter<String, DefaultStrategyFactory.DynamicContext, String> {

    @Override
    protected void prepareDataByMultiThread(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        // 多线程加载业务数据
    }

}
