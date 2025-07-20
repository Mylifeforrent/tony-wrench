package com.study.tony.wrench.ratelimiter.link.simplemode.factory;

import com.study.tony.wrench.ratelimiter.link.simplemode.ILogicChainNode;
import com.study.tony.wrench.ratelimiter.link.simplemode.logic.SimpleRuleLogic01;
import com.study.tony.wrench.ratelimiter.link.simplemode.logic.SimpleRuleLogic02;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SimpleTradeRuleFactory {

    @Resource
    private SimpleRuleLogic01 ruleLogic101;
    @Resource
    private SimpleRuleLogic02 ruleLogic102;

    public ILogicChainNode<String, DynamicContext, String> openLogicLink() {
        ruleLogic101.appendNext(ruleLogic102);
        return ruleLogic101;
    }

    //这个时候如果在节点1上面添加节点3，你就会发现openLogicLink这个链路被你破坏了，毕竟ruleLogic101时单例的，除非你给他改一下scope，让他不采用默认的spring单例模式，
    //每次就时一个新的bean也就不会相互影响了。这就是简单的责任链设计模式的一个缺点吧，就是链路之间的耦合度太高了，导致你在添加新的节点的时候需要去修改原有的链路逻辑。
//    public ILogicChainNode<String, DynamicContext, String> openLogicLink2() {
//        ruleLogic101.appendNext(ruleLogic103);
//        return ruleLogic101;
//    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {
        private String age;
    }

}
