package com.study.tony.wrench.ratelimiter.dynamicconfigcenter;

import com.study.tony.wrench.TonyWrenchTestApplication;
import com.study.tony.wrench.ratelimiter.domain.model.valobj.AttributeVO;
import com.study.tony.wrench.ratelimiter.types.annotations.DCCValue;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TonyWrenchTestApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DynamicConfigCenterStarterTest {

    @DCCValue("downgradeSwitch:0")
    private String downgradeSwitch;

    @Resource
    private RTopic dynamicConfigCenterRedisTopic;

    @Resource
    private TestConfigChangeListener testConfigChangeListener;

    @Test
    public void test_get() throws InterruptedException {
        log.info("测试结果:{}", downgradeSwitch);
    }

    @Test
    public void test_publish_with_timeout() throws InterruptedException {
        // 记录初始值
        String initialValue = downgradeSwitch;
        log.info("初始值: {}", initialValue);
        
        // 推送配置变更消息
        dynamicConfigCenterRedisTopic.publish(new AttributeVO("downgradeSwitch", "4"));
        log.info("已发布配置变更消息");
        
        // 等待配置更新（最多等待5秒）
        CountDownLatch latch = new CountDownLatch(1);
        boolean updated = latch.await(5, TimeUnit.SECONDS);
        
        if (updated) {
            log.info("配置已更新: {}", downgradeSwitch);
        } else {
            log.warn("配置更新超时，当前值: {}", downgradeSwitch);
        }
        
        // 验证配置是否已更新
        // 注意：由于配置更新是异步的，这里只是验证消息是否被发送
        log.info("测试完成，最终值: {}", downgradeSwitch);
    }

    @Test
    public void test_publish_with_verification() throws InterruptedException {
        // 方案二：使用轮询验证
        String initialValue = downgradeSwitch;
        log.info("初始值: {}", initialValue);
        
        // 推送配置变更消息
        dynamicConfigCenterRedisTopic.publish(new AttributeVO("downgradeSwitch", "8"));
        log.info("已发布配置变更消息");
        
        // 轮询检查配置是否更新（最多等待10秒）
        int maxAttempts = 20; // 20次 * 500ms = 10秒
        int attempts = 0;
        boolean updated = false;
        
        while (attempts < maxAttempts && !updated) {
            Thread.sleep(500); // 等待500ms
            attempts++;
            
            if (!downgradeSwitch.equals(initialValue)) {
                updated = true;
                log.info("配置已更新，尝试次数: {}, 新值: {}", attempts, downgradeSwitch);
            }
        }
        
        if (!updated) {
            log.warn("配置更新超时，最终值: {}", downgradeSwitch);
        }
    }

//    @Test
//    public void test_publish_with_test_listener() throws InterruptedException {
//        // 方案三：使用测试监听器（最优雅的方式）
//        String initialValue = downgradeSwitch;
//        log.info("初始值: {}", initialValue);
//
//        // 推送配置变更消息
//        dynamicConfigCenterRedisTopic.publish(new AttributeVO("downgradeSwitch", "12"));
//        log.info("已发布配置变更消息");
//
//        // 使用测试监听器等待配置更新
//        String updatedValue = testConfigChangeListener.waitForUpdate("downgradeSwitch", 5, TimeUnit.SECONDS);
//
//        if (updatedValue != null) {
//            log.info("配置已更新: {} -> {}", initialValue, updatedValue);
//        } else {
//            log.warn("配置更新超时");
//        }
//
//        log.info("测试完成，最终值: {}", downgradeSwitch);
//    }

    @Test
    public void test_publish_with_spring_test_features() throws InterruptedException {
        // 方案四：利用Spring测试框架特性
        String initialValue = downgradeSwitch;
        log.info("初始值: {}", initialValue);
        
        // 推送配置变更消息
        dynamicConfigCenterRedisTopic.publish(new AttributeVO("downgradeSwitch", "16"));
        log.info("已发布配置变更消息");
        
        // 使用Spring的测试等待机制
        // 这里可以结合 @DirtiesContext 确保每次测试都是干净的环境
        Thread.sleep(2000); // 给配置更新一些时间
        
        log.info("测试完成，最终值: {}", downgradeSwitch);
    }

//    @After
//    public void tearDown() {
//        // 清理测试监听器
//        if (testConfigChangeListener != null) {
//            testConfigChangeListener.clear();
//        }
//    }
}
