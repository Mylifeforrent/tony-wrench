package com.study.tony.wrench.design.framework.ruletree;

import com.study.tony.wrench.design.framework.TonyWrenchTestApplication;
import com.study.tony.wrench.design.framework.ruletree.factory.DefaultStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TonyWrenchTestApplication.class)
public class DesignFrameworkTest {

    @Resource
    private DefaultStrategyFactory defaultStrategyFactory;

    @Test
    public void test() throws Exception {
        IStrategyHandler<String, DefaultStrategyFactory.DynamicContext, String> strategyHandler = defaultStrategyFactory.strategyHandler();
        String result = strategyHandler.apply("rootnode", new DefaultStrategyFactory.DynamicContext());

        log.info("测试结果:{}", result);
    }

}
