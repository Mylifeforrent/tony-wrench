# Guava RateLimiter 令牌桶机制详解

## 1. 令牌桶算法基础

### 1.1 什么是令牌桶算法

令牌桶算法是一种**流量整形**和**限流**算法，用于控制系统的请求处理速率。

**核心概念：**
- **令牌桶**: 一个固定容量的桶，用于存储令牌
- **令牌生成**: 以固定速率向桶中放入令牌
- **令牌消费**: 请求需要获取令牌才能被处理
- **桶容量**: 桶能存储的最大令牌数量

### 1.2 令牌桶工作流程

```
令牌桶
├── 容量: 最大令牌数
├── 速率: 每秒产生令牌数
└── 当前令牌数: 可用令牌数量

请求 → 尝试获取令牌 → 成功/失败
```

## 2. tryAcquire() 方法详解

### 2.1 方法签名

```java
// 基本用法
boolean success = rateLimiter.tryAcquire();  // 尝试获取1个令牌
boolean success = rateLimiter.tryAcquire(5); // 尝试获取5个令牌
boolean success = rateLimiter.tryAcquire(1, 100, TimeUnit.MILLISECONDS); // 等待100ms
```

### 2.2 返回值说明

- **`true`**: 成功获取令牌，请求可以继续
- **`false`**: 令牌不足，请求被限流

### 2.3 核心机制

```java
// 创建限流器：每秒产生10个令牌
RateLimiter rateLimiter = RateLimiter.create(10.0);

// 请求处理
if (!rateLimiter.tryAcquire()) {
    // 令牌不足，请求被限流
    return "请求过于频繁";
} else {
    // 获取到令牌，处理请求
    return processRequest();
}
```

## 3. 令牌生成和消费机制

### 3.1 令牌生成机制

```java
// 令牌生成示例
RateLimiter rateLimiter = RateLimiter.create(10.0); // 每秒10个令牌

// 时间线分析
// T=0s: 桶中有10个令牌
// T=0.1s: 桶中有11个令牌 (10 + 1)
// T=0.2s: 桶中有12个令牌 (10 + 2)
// ...
// T=1s: 桶中有20个令牌 (10 + 10)
```

### 3.2 令牌消费机制

```java
// 令牌消费示例
RateLimiter rateLimiter = RateLimiter.create(10.0);

// 连续请求
System.out.println(rateLimiter.tryAcquire()); // true, 消耗1个令牌
System.out.println(rateLimiter.tryAcquire()); // true, 消耗1个令牌
// ... 连续10次都是true

System.out.println(rateLimiter.tryAcquire()); // false, 令牌不足
// 等待0.1秒后
Thread.sleep(100);
System.out.println(rateLimiter.tryAcquire()); // true, 又有新令牌了
```

## 4. 实际应用示例

### 4.1 简单限流

```java
@Aspect
@Component
public class SimpleRateLimiterAspect {
    
    private final RateLimiter rateLimiter = RateLimiter.create(10.0); // 每秒10个请求
    
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint jp, RateLimit rateLimit) throws Throwable {
        if (!rateLimiter.tryAcquire()) {
            return "系统繁忙，请稍后重试";
        }
        return jp.proceed();
    }
}
```

### 4.2 用户级别限流

```java
@Component
public class UserRateLimiter {
    
    private final LoadingCache<String, RateLimiter> userLimiters = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(key -> RateLimiter.create(5.0)); // 每个用户每秒5个请求
    
    public boolean tryAcquire(String userId) {
        RateLimiter limiter = userLimiters.get(userId);
        return limiter.tryAcquire();
    }
}
```

### 4.3 带等待的限流

```java
@Aspect
@Component
public class WaitRateLimiterAspect {
    
    private final RateLimiter rateLimiter = RateLimiter.create(10.0);
    
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint jp, RateLimit rateLimit) throws Throwable {
        // 尝试获取令牌，最多等待500ms
        if (!rateLimiter.tryAcquire(1, 500, TimeUnit.MILLISECONDS)) {
            return "请求超时，请稍后重试";
        }
        return jp.proceed();
    }
}
```

## 5. 令牌桶 vs 其他限流算法

| 算法 | 特点 | 适用场景 |
|------|------|----------|
| **令牌桶** | 固定速率生成令牌，突发流量友好 | API限流、用户行为控制 |
| **漏桶** | 固定速率处理请求，平滑流量 | 系统资源保护 |
| **滑动窗口** | 精确控制时间窗口内的请求数 | 精确限流控制 |
| **计数器** | 简单计数，实现简单 | 简单限流场景 |

## 6. 高级用法

### 6.1 预热模式

```java
// 预热模式：系统启动时逐渐提升限流能力
RateLimiter rateLimiter = RateLimiter.create(10.0, 3, TimeUnit.SECONDS);
// 3秒内从0逐渐提升到每秒10个令牌
```

### 6.2 动态调整

```java
@Component
public class DynamicRateLimiter {
    
    private RateLimiter rateLimiter = RateLimiter.create(10.0);
    
    public void setRate(double permitsPerSecond) {
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }
    
    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }
}
```

### 6.3 批量处理

```java
@Aspect
@Component
public class BatchRateLimiterAspect {
    
    private final RateLimiter rateLimiter = RateLimiter.create(100.0); // 每秒100个
    
    @Around("@annotation(batchProcess)")
    public Object around(ProceedingJoinPoint jp, BatchProcess batchProcess) throws Throwable {
        int batchSize = batchProcess.batchSize();
        
        // 尝试获取批量令牌
        if (!rateLimiter.tryAcquire(batchSize)) {
            return "批量处理请求过于频繁";
        }
        
        return jp.proceed();
    }
}
```

## 7. 性能考虑

### 7.1 内存使用

```java
// 用户级别限流的内存优化
private final LoadingCache<String, RateLimiter> userLimiters = Caffeine.newBuilder()
    .maximumSize(10000)           // 最大缓存10000个用户
    .expireAfterWrite(5, TimeUnit.MINUTES)  // 5分钟过期
    .build(key -> RateLimiter.create(5.0));
```

### 7.2 并发安全

```java
// RateLimiter 本身是线程安全的
// 无需额外的同步机制
RateLimiter rateLimiter = RateLimiter.create(10.0);

// 多线程并发访问
CompletableFuture.allOf(
    CompletableFuture.runAsync(() -> rateLimiter.tryAcquire()),
    CompletableFuture.runAsync(() -> rateLimiter.tryAcquire()),
    CompletableFuture.runAsync(() -> rateLimiter.tryAcquire())
).join();
```

## 8. 实际项目中的应用

### 8.1 限流器 AOP 实现

```java
@Aspect
@Component
public class RateLimiterAOP {
    
    private final LoadingCache<String, RateLimiter> loginRecord = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(key -> RateLimiter.create(10.0));
    
    @Around("@annotation(rateLimiterAccessInterceptor)")
    public Object around(ProceedingJoinPoint jp, RateLimiterAccessInterceptor interceptor) throws Throwable {
        String keyAttr = getKeyAttr(jp);
        
        // 获取限流器
        RateLimiter rateLimiter = loginRecord.getIfPresent(keyAttr);
        if (null == rateLimiter) {
            rateLimiter = RateLimiter.create(interceptor.permitsPerSecond());
            loginRecord.put(keyAttr, rateLimiter);
        }
        
        // 限流拦截
        if (!rateLimiter.tryAcquire()) {
            log.info("限流-超频次拦截：{}", keyAttr);
            return fallbackMethodResult(jp, interceptor.fallbackMethod());
        }
        
        // 返回结果
        return jp.proceed();
    }
}
```

### 8.2 黑名单机制结合

```java
@Aspect
@Component
public class AdvancedRateLimiterAOP {
    
    private final LoadingCache<String, RateLimiter> loginRecord = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(key -> RateLimiter.create(10.0));
    
    private final LoadingCache<String, Long> blacklist = Caffeine.newBuilder()
        .expireAfterWrite(24, TimeUnit.HOURS)
        .build();
    
    @Around("@annotation(interceptor)")
    public Object around(ProceedingJoinPoint jp, RateLimiterAccessInterceptor interceptor) throws Throwable {
        String keyAttr = getKeyAttr(jp);
        
        // 黑名单拦截
        if (!"all".equals(keyAttr)
                && interceptor.blacklistCount() != 0
                && null != blacklist.getIfPresent(keyAttr)
                && blacklist.getIfPresent(keyAttr) > interceptor.blacklistCount()) {
            log.info("限流-黑名单拦截(24h)：{}", keyAttr);
            return fallbackMethodResult(jp, interceptor.fallbackMethod());
        }
        
        // 获取限流器
        RateLimiter rateLimiter = loginRecord.getIfPresent(keyAttr);
        if (null == rateLimiter) {
            rateLimiter = RateLimiter.create(interceptor.permitsPerSecond());
            loginRecord.put(keyAttr, rateLimiter);
        }
        
        // 限流拦截
        if (!rateLimiter.tryAcquire()) {
            // 记录黑名单计数
            if (interceptor.blacklistCount() != 0) {
                if (null == blacklist.getIfPresent(keyAttr)) {
                    blacklist.put(keyAttr, 1L);
                } else {
                    blacklist.put(keyAttr, blacklist.getIfPresent(keyAttr) + 1L);
                }
            }
            log.info("限流-超频次拦截：{}", keyAttr);
            return fallbackMethodResult(jp, interceptor.fallbackMethod());
        }
        
        return jp.proceed();
    }
}
```

## 9. 最佳实践

### 9.1 配置建议

```java
// 根据系统负载动态调整限流参数
@Component
public class AdaptiveRateLimiter {
    
    private final RateLimiter rateLimiter = RateLimiter.create(10.0);
    
    @EventListener
    public void onSystemLoadChange(SystemLoadEvent event) {
        double load = event.getLoad();
        if (load > 0.8) {
            rateLimiter.setRate(5.0); // 高负载时降低限流
        } else if (load < 0.3) {
            rateLimiter.setRate(20.0); // 低负载时提高限流
        }
    }
}
```

### 9.2 监控和告警

```java
@Component
public class RateLimiterMonitor {
    
    private final Counter limitedRequests = Counter.builder("rate_limited_requests")
        .description("Number of rate limited requests")
        .register(Metrics.globalRegistry);
    
    public void recordLimitedRequest(String userId) {
        limitedRequests.increment();
        log.warn("用户 {} 被限流", userId);
    }
}
```

### 9.3 降级策略

```java
@Component
public class RateLimiterFallback {
    
    public Object fallback(ProceedingJoinPoint jp, String fallbackMethod) {
        try {
            // 调用降级方法
            Method method = jp.getTarget().getClass().getMethod(fallbackMethod);
            return method.invoke(jp.getTarget());
        } catch (Exception e) {
            return "系统繁忙，请稍后重试";
        }
    }
}
```

## 10. 总结

### 10.1 核心特点

1. **令牌桶算法**: 固定速率生成令牌，支持突发流量
2. **非阻塞获取**: 立即返回结果，不等待
3. **线程安全**: 支持多线程并发访问
4. **高性能**: 基于高效的时间计算算法
5. **灵活配置**: 支持动态调整限流参数

### 10.2 适用场景

- **API 接口保护**: 防止接口被恶意调用
- **用户行为控制**: 限制单个用户的访问频率
- **系统资源保护**: 防止系统过载
- **业务规则控制**: 实现业务层面的访问控制

### 10.3 关键优势

- **突发流量友好**: 支持短时间的突发请求
- **精确控制**: 可以精确控制请求处理速率
- **低延迟**: 非阻塞设计，响应速度快
- **易于集成**: 与 Spring AOP 等框架无缝集成

通过合理使用 Guava RateLimiter，可以有效保护系统免受恶意攻击和过载，同时提供良好的用户体验。 