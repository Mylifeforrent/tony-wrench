package com.study.tony.wrench.design.framework.ruletree.factory;

import com.study.tony.wrench.design.framework.ruletree.AbstractMultiThreadStrategyRouter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractBusinessXxxSupport extends AbstractMultiThreadStrategyRouter<String, DefaultStrategyFactory.DynamicContext, String> {

    @Override
    protected void prepareDataByMultiThread(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws Exception {
        // 多线程加载业务数据
    }

}
