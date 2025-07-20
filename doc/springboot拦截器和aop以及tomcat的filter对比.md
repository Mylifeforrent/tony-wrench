我来详细解释 Spring Boot 中 Interceptor、AOP 注解和 Tomcat Filter 的拦截机制区别，包括拦截顺序、实现原理和执行流程。

## 1. 拦截顺序对比

### 整体执行顺序
```
HTTP请求 → Tomcat Filter → Spring Interceptor → AOP → Controller方法 → AOP → Spring Interceptor → Tomcat Filter → HTTP响应
```

**具体顺序：**
1. **Tomcat Filter** (最外层)
2. **Spring Interceptor** (preHandle)
3. **AOP** (@Before)
4. **Controller方法执行**
5. **AOP** (@After/@AfterReturning/@AfterThrowing)
6. **Spring Interceptor** (postHandle)
7. **Spring Interceptor** (afterCompletion)
8. **Tomcat Filter** (最外层)

## 2. 实现原理对比

### 2.1 Tomcat Filter
**实现原理：** 基于 Servlet 规范，在 Servlet 容器层面进行拦截
```java
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        // 前置处理
        System.out.println("Filter: 请求开始");
        
        // 调用下一个过滤器或目标资源
        chain.doFilter(request, response);
        
        // 后置处理
        System.out.println("Filter: 请求结束");
    }
}
```

### 2.2 Spring Interceptor
**实现原理：** 基于 Spring MVC 的 HandlerInterceptor 接口
```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler) throws Exception {
        System.out.println("Interceptor: preHandle");
        return true; // 返回false会中断请求
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("Interceptor: postHandle");
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) throws Exception {
        System.out.println("Interceptor: afterCompletion");
    }
}
```

### 2.3 Spring AOP
**实现原理：** 基于动态代理（JDK动态代理或CGLIB）
```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("@annotation(com.example.Logging)")
    public void beforeMethod(JoinPoint joinPoint) {
        System.out.println("AOP: @Before");
    }
    
    @After("@annotation(com.example.Logging)")
    public void afterMethod(JoinPoint joinPoint) {
        System.out.println("AOP: @After");
    }
    
    @AfterReturning("@annotation(com.example.Logging)")
    public void afterReturning(JoinPoint joinPoint) {
        System.out.println("AOP: @AfterReturning");
    }
    
    @AfterThrowing("@annotation(com.example.Logging)")
    public void afterThrowing(JoinPoint joinPoint) {
        System.out.println("AOP: @AfterThrowing");
    }
}
```

## 3. 详细执行流程示例

### 3.1 完整示例代码

**1. Filter 配置**
```java
@Configuration
public class FilterConfig {
    
    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
```

**2. Interceptor 配置**
```java
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    
    @Autowired
    private LoggingInterceptor loggingInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .order(1);
    }
}
```

**3. Controller 示例**
```java
@RestController
@RequestMapping("/api")
public class UserController {
    
    @Logging
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        System.out.println("Controller: 执行业务逻辑");
        return ResponseEntity.ok(new User(id, "张三"));
    }
}
```

**4. 自定义注解**
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
}
```

### 3.2 执行流程演示

当访问 `GET /api/users/1` 时，执行顺序如下：

```
1. Tomcat Filter - doFilter() 开始
   ↓
2. Spring Interceptor - preHandle()
   ↓
3. Spring AOP - @Before
   ↓
4. Controller方法执行 - getUser()
   ↓
5. Spring AOP - @AfterReturning (如果成功) 或 @AfterThrowing (如果异常)
   ↓
6. Spring AOP - @After
   ↓
7. Spring Interceptor - postHandle()
   ↓
8. Spring Interceptor - afterCompletion()
   ↓
9. Tomcat Filter - doFilter() 结束
```

## 4. 各机制的特点对比

| 特性 | Tomcat Filter | Spring Interceptor | Spring AOP |
|------|---------------|-------------------|------------|
| **作用范围** | Servlet容器级别 | Spring MVC级别 | 方法级别 |
| **拦截粒度** | 粗粒度（URL级别） | 中粒度（Handler级别） | 细粒度（方法级别） |
| **执行时机** | 请求进入Servlet容器时 | 请求进入Spring MVC时 | 方法调用时 |
| **异常处理** | 可以捕获所有异常 | 可以捕获所有异常 | 可以捕获方法异常 |
| **性能影响** | 最小 | 中等 | 较大（代理开销） |
| **配置复杂度** | 简单 | 中等 | 复杂 |
| **适用场景** | 跨域、编码、安全 | 日志、权限、参数校验 | 业务逻辑、事务、缓存 |

## 5. 实际应用场景

### 5.1 Tomcat Filter 适用场景
```java
// 跨域过滤器
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        chain.doFilter(request, response);
    }
}
```

### 5.2 Spring Interceptor 适用场景
```java
// 权限拦截器
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            response.setStatus(401);
            return false;
        }
        // 验证token逻辑
        return true;
    }
}
```

### 5.3 Spring AOP 适用场景
```java
// 事务管理
@Aspect
@Component
public class TransactionAspect {
    
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object aroundTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        // 开启事务
        TransactionStatus status = transactionManager.beginTransaction();
        try {
            Object result = joinPoint.proceed();
            // 提交事务
            transactionManager.commit(status);
            return result;
        } catch (Exception e) {
            // 回滚事务
            transactionManager.rollback(status);
            throw e;
        }
    }
}
```

## 6. 性能考虑

### 6.1 执行开销对比
```
Filter < Interceptor < AOP
```

### 6.2 选择建议
- **简单拦截**：使用 Filter
- **Spring MVC 相关拦截**：使用 Interceptor
- **业务逻辑拦截**：使用 AOP
- **多层拦截**：组合使用，注意执行顺序

通过以上详细对比，你可以根据具体需求选择合适的拦截机制。每种机制都有其适用场景，合理组合使用可以达到最佳的拦截效果。