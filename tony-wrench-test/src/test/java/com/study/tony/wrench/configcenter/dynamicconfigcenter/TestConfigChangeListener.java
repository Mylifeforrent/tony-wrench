package com.study.tony.wrench.configcenter.dynamicconfigcenter;

import com.study.tony.wrench.configcenter.domain.model.valobj.AttributeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 测试专用的配置变更监听器
 * 
 * 用于在测试中优雅地处理异步配置更新，避免使用 CountDownLatch(1).await()
 * 
 * 使用方式：
 * 1. 在测试中注入这个监听器
 * 2. 发布配置变更消息
 * 3. 使用 waitForUpdate() 方法等待配置更新
 * 4. 验证配置是否按预期更新
 * 
 * @author Tony
 */
@Slf4j
@Component
public class TestConfigChangeListener {

    /**
     * 存储等待配置更新的 Future
     * Key: 属性名
     * Value: 对应的 CompletableFuture
     */
    private final ConcurrentHashMap<String, CompletableFuture<String>> waitingUpdates = new ConcurrentHashMap<>();

    /**
     * 等待指定属性的配置更新
     * 
     * @param attributeName 属性名
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 更新后的值，如果超时返回 null
     */
    public String waitForUpdate(String attributeName, long timeout, TimeUnit unit) {
        try {
            CompletableFuture<String> future = waitingUpdates.computeIfAbsent(
                attributeName, 
                k -> new CompletableFuture<>()
            );
            
            return future.get(timeout, unit);
        } catch (Exception e) {
            log.warn("等待配置更新超时: {}", attributeName, e);
            return null;
        } finally {
            // 清理已完成的 Future
            waitingUpdates.remove(attributeName);
        }
    }

    /**
     * 处理配置变更消息
     * 
     * 这个方法会被动态配置中心的监听器调用
     * 当收到配置变更消息时，完成对应的 Future
     * 
     * @param attributeVO 配置变更消息
     */
    public void onConfigChange(AttributeVO attributeVO) {
        String attributeName = attributeVO.getAttribute();
        String newValue = attributeVO.getValue();
        
        log.info("测试监听器收到配置变更: {} = {}", attributeName, newValue);
        
        // 完成对应的 Future
        CompletableFuture<String> future = waitingUpdates.get(attributeName);
        if (future != null) {
            future.complete(newValue);
            log.info("已完成配置更新等待: {} = {}", attributeName, newValue);
        }
    }

    /**
     * 清理所有等待的 Future
     * 
     * 在测试结束时调用，避免内存泄漏
     */
    public void clear() {
        waitingUpdates.clear();
        log.info("已清理所有等待的配置更新");
    }
} 