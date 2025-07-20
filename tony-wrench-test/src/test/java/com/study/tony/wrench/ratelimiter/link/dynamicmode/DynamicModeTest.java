package com.study.tony.wrench.ratelimiter.link.dynamicmode;

import com.alibaba.fastjson.JSON;
import com.study.tony.wrench.ratelimiter.link.dynamicmode.chain.BusinessLogicChain;
import com.study.tony.wrench.ratelimiter.link.dynamicmode.factory.DynamicTradeRuleFactory;
import com.study.tony.wrench.ratelimiter.link.dynamicmode.logic.XxxResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DynamicModeTest {

    @Resource(name = "demo01")
    private BusinessLogicChain<String, DynamicTradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList01;

    @Resource(name = "demo02")
    private BusinessLogicChain<String, DynamicTradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList02;

    @Test
    public void test_model02_01() throws Exception {
        XxxResponse apply = businessLinkedList01.apply("123", new DynamicTradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(apply));
    }

    @Test
    public void test_model02_02() throws Exception {
        XxxResponse apply = businessLinkedList02.apply("123", new DynamicTradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(apply));
    }

}
