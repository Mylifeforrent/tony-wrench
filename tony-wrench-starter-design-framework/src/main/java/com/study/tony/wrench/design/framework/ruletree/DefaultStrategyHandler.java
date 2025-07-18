package com.study.tony.wrench.design.framework.ruletree;

public class DefaultStrategyHandler implements IStrategyHandler<String, String, String> {

    @Override
    public String apply(String inputParams, String dynamicContext) throws Exception {
        // 缺省的规则处理逻辑
        return null; // 返回 null 或者其他默认值
    }
}
