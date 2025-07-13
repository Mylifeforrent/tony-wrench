# 动态配置中心代码加载顺序与运作机制详解

## 概述

动态配置中心（Dynamic Config Center，DCC）是一个基于Spring Boot自动配置和Redis的分布式配置管理组件。它支持运行时动态更新配置，无需重启应用。

## 核心组件架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring Boot 应用启动                      │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                 spring.factories 自动配置注册                │
│  org.springframework.boot.autoconfigure.EnableAutoConfiguration │
│  = DynamicConfigCenterRegisterAutoConfig,                    │
│    DynamicConfigCenterAutoConfig                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│              DynamicConfigCenterRegisterAutoConfig          │
│                     (第一个自动配置类)                        │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│              DynamicConfigCenterAutoConfig                  │
│                     (第二个自动配置类)                        │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                     Bean 创建和初始化                        │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                 BeanPostProcessor 处理                       │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    Redis 主题监听                            │
└─────────────────────────────────────────────────────────────┘
```

## 详细加载顺序

### 1. Spring Boot 启动阶段

#### 1.1 spring.factories 文件加载
**文件位置**: `META-INF/spring.factories`
**作用**: 注册自动配置类，定义加载顺序

```properties
# 按照这个顺序加载自动配置类
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
cn.bugstack.wrench.dynamic.config.center.config.DynamicConfigCenterRegisterAutoConfig,\
cn.bugstack.wrench.dynamic.config.center.config.DynamicConfigCenterAutoConfig
```

**加载顺序**:
1. `DynamicConfigCenterRegisterAutoConfig` - 负责注册和初始化核心组件
2. `DynamicConfigCenterAutoConfig` - 负责Bean后处理，实现动态配置功能

### 2. 第一个自动配置类：DynamicConfigCenterRegisterAutoConfig

#### 2.1 配置属性绑定
**作用**: 启用配置属性绑定，读取application.yml中的配置

```java
@EnableConfigurationProperties(value = {
    DynamicConfigCenterAutoProperties.class,        // xfg.wrench.config.*
    DynamicConfigCenterRegisterAutoProperties.class // xfg.wrench.config.register.*
})
```

#### 2.2 Bean创建顺序

**2.2.1 创建Redis客户端 (redissonClient)**
```java
@Bean("xfgWrenchRedissonClient")
public RedissonClient redissonClient(DynamicConfigCenterRegisterAutoProperties properties)
```
- **时机**: 第一个被创建的Bean
- **作用**: 建立Redis连接，为后续操作提供支持
- **配置**: 连接池、超时、重试等参数

**2.2.2 创建动态配置服务 (dynamicConfigCenterService)**
```java
@Bean
public IDynamicConfigCenterService dynamicConfigCenterService(...)
```
- **时机**: 第二个被创建的Bean
- **依赖**: Redis客户端
- **作用**: 核心服务实现，负责配置扫描、注入和管理

**2.2.3 创建配置变更监听器 (dynamicConfigCenterAdjustListener)**
```java
@Bean
public DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener(...)
```
- **时机**: 第三个被创建的Bean
- **依赖**: 动态配置服务
- **作用**: 监听Redis主题消息，处理配置变更

**2.2.4 创建Redis主题订阅 (dynamicConfigCenterRedisTopic)**
```java
@Bean(name = "dynamicConfigCenterRedisTopic")
public RTopic threadPoolConfigAdjustListener(...)
```
- **时机**: 最后一个被创建的Bean
- **作用**: 创建Redis主题，绑定监听器，实现配置变更通知机制

### 3. 第二个自动配置类：DynamicConfigCenterAutoConfig

#### 3.1 BeanPostProcessor 注册
**作用**: 实现BeanPostProcessor接口，参与Bean生命周期

```java
@Configuration
public class DynamicConfigCenterAutoConfig implements BeanPostProcessor
```

#### 3.2 Bean后处理时机
**调用时机**: 每个Bean初始化完成后
**处理逻辑**: 扫描Bean中的@DCCValue注解，注入配置值

```java
@Override
public Object postProcessAfterInitialization(Object bean, String beanName) {
    return dynamicConfigCenterService.proxyObject(bean);
}
```

### 4. Bean创建和配置注入阶段

#### 4.1 应用Bean创建
Spring开始创建应用中的其他Bean（Controller、Service等）

#### 4.2 Bean后处理
每个Bean创建完成后，都会经过`DynamicConfigCenterAutoConfig`的后处理器：

1. **扫描@DCCValue注解**
2. **解析配置值格式** (属性名:默认值)
3. **从Redis读取配置**
4. **注入配置值到Bean字段**
5. **注册Bean到管理映射**

### 5. 运行时配置变更处理

#### 5.1 Redis主题监听
**主题名称**: `DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_系统名`
**监听器**: `DynamicConfigCenterAdjustListener`

#### 5.2 配置变更流程
1. **外部系统修改配置** → 发布消息到Redis主题
2. **监听器接收消息** → 调用`onMessage`方法
3. **服务处理变更** → 调用`adjustAttributeValue`方法
4. **更新Bean字段** → 通过反射设置新值

## 核心运作机制

### 1. 配置扫描机制

#### 1.1 注解识别
```java
@DCCValue("isSwitch:true")
private String isSwitch;
```

#### 1.2 配置解析
- **属性名**: `isSwitch`
- **默认值**: `true`
- **Redis键**: `系统名_isSwitch`

### 2. 配置存储机制

#### 2.1 Redis键命名规则
```
格式: 系统名_属性名
示例: user-service_isSwitch
```

#### 2.2 配置值管理
- **首次启动**: 使用默认值，写入Redis
- **后续启动**: 从Redis读取最新值
- **运行时变更**: 实时更新Redis和Bean字段

### 3. 动态更新机制

#### 3.1 发布-订阅模式
- **发布者**: 外部配置管理系统
- **订阅者**: 应用中的监听器
- **消息格式**: AttributeVO对象

#### 3.2 实时同步
1. **配置变更** → Redis主题发布消息
2. **监听器接收** → 解析AttributeVO
3. **查找Bean** → 从dccBeanGroup映射中查找
4. **更新字段** → 通过反射设置新值

### 4. AOP代理处理

#### 4.1 代理对象识别
```java
if (AopUtils.isAopProxy(bean)) {
    targetBeanClass = AopUtils.getTargetClass(bean);
    targetBeanObject = AopProxyUtils.getSingletonTarget(bean);
}
```

#### 4.2 真实对象获取
- **代理类**: 无法直接获取注解
- **目标类**: 通过AopUtils获取真实类
- **目标对象**: 通过AopProxyUtils获取真实对象

## 配置示例

### 1. application.yml 配置
```yaml
xfg:
  wrench:
    config:
      system: user-service  # 系统名称
      register:
        host: localhost     # Redis地址
        port: 6379         # Redis端口
        password: 123456   # Redis密码
        pool-size: 64      # 连接池大小
```

### 2. 使用@DCCValue注解
```java
@Component
public class UserService {
    @DCCValue("maxThreads:10")
    private String maxThreads;
    
    @DCCValue("timeout:5000")
    private String timeout;
    
    @DCCValue("isSwitch:true")
    private String isSwitch;
}
```

### 3. 配置变更消息
```java
// 发布配置变更消息
AttributeVO attributeVO = new AttributeVO("isSwitch", "false");
redisTemplate.convertAndSend("DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_user-service", attributeVO);
```

## 关键特性

### 1. 零侵入性
- 只需添加@DCCValue注解
- 无需修改业务逻辑
- 支持现有代码无缝集成

### 2. 实时性
- 配置变更立即生效
- 无需重启应用
- 支持热更新

### 3. 分布式支持
- 基于Redis的分布式存储
- 支持多实例配置同步
- 系统级别的配置隔离

### 4. 高可用性
- Redis连接池管理
- 异常处理和重试机制
- 配置默认值保护

## 总结

动态配置中心通过Spring Boot的自动配置机制，在应用启动时按照预定义的顺序创建和初始化各个组件。通过BeanPostProcessor在Bean生命周期中注入配置，通过Redis主题实现配置的实时同步。整个机制设计巧妙，既保证了配置的实时性，又确保了系统的稳定性和可扩展性。 