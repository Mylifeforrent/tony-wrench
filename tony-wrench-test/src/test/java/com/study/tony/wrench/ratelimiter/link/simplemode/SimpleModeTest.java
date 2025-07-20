package com.study.tony.wrench.ratelimiter.link.simplemode;

import com.alibaba.fastjson.JSON;
import com.study.tony.wrench.ratelimiter.link.simplemode.factory.SimpleTradeRuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleModeTest {

    @Resource
    public SimpleTradeRuleFactory simpleTradeRuleFactory;

    @Test
    public void test_model01_01() throws Exception {
        ILogicChainNode<String, SimpleTradeRuleFactory.DynamicContext, String> logicLink = simpleTradeRuleFactory.openLogicLink();
        String logic = logicLink.apply("123", new SimpleTradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(logic));
    }

}
