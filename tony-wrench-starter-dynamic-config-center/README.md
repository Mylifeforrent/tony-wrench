# Tony Wrench Dynamic Config Center Starter

## 概述

Tony Wrench Dynamic Config Center Starter 是一个 Spring Boot Starter，用于实现动态配置中心功能。它支持基于 Redis 的配置动态更新，无需重启应用即可生效。

## 功能特性

- ✅ 基于 Redis 的动态配置存储
- ✅ 配置热更新，无需重启应用
- ✅ 支持多系统配置隔离
- ✅ 配置变更实时通知
- ✅ 完整的配置属性支持
- ✅ IDEA 自动补全支持

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.study.tony.wrench</groupId>
    <artifactId>tony-wrench-starter-dynamic-config-center</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 配置属性

在 `application.yml` 中添加配置：

```yaml
tony:
  wrench:
    config:
      # 系统名称 - 必填
      system: user-service
      
      # 是否启用动态配置中心 - 可选，默认：true
      enabled: true
      
      # 配置刷新间隔（毫秒）- 可选，默认：5000
      refresh-interval: 5000
      
      # Redis键前缀 - 可选，默认：tony_wrench_config
      key-prefix: tony_wrench_config
```

### 3. 使用配置

```java
@Service
public class UserService {
    
    @Autowired
    private DynamicConfigCenterAutoProperties configProperties;
    
    public void someMethod() {
        // 获取系统名称
        String systemName = configProperties.getSystem();
        
        // 生成Redis键名
        String redisKey = configProperties.getKey("user.maxCount");
        // 结果：user-service_user.maxCount
        
        // 生成带前缀的Redis键名
        String fullKey = configProperties.getKeyWithPrefix("user.maxCount");
        // 结果：tony_wrench_config_user-service_user.maxCount
    }
}
```

## 配置属性详解

### 核心配置

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `tony.wrench.config.system` | String | `default-system` | 系统名称，用于标识当前应用 |
| `tony.wrench.config.enabled` | boolean | `true` | 是否启用动态配置中心 |
| `tony.wrench.config.refresh-interval` | long | `5000` | 配置刷新间隔（毫秒） |
| `tony.wrench.config.key-prefix` | String | `tony_wrench_config` | Redis键前缀 |

### 配置示例

#### 基础配置
```yaml
tony:
  wrench:
    config:
      system: user-service
```

#### 完整配置
```yaml
tony:
  wrench:
    config:
      system: user-service
      enabled: true
      refresh-interval: 5000
      key-prefix: tony_wrench_config
```

#### 禁用配置
```yaml
tony:
  wrench:
    config:
      system: user-service
      enabled: false  # 禁用动态配置中心
```

## IDEA 自动补全

### 启用自动补全

1. 确保项目中包含 `spring-boot-configuration-processor` 依赖
2. 重新构建项目：`mvn clean compile`
3. 重启 IDEA

### 使用自动补全

在 `application.yml` 中输入 `tony.wrench.config.` 时，IDEA 会显示所有可用的配置属性：

```yaml
tony:
  wrench:
    config:
      system: user-service        # ✅ 自动补全
      enabled: true              # ✅ 自动补全
      refresh-interval: 5000     # ✅ 自动补全
      key-prefix: my_prefix      # ✅ 自动补全
```

### 配置验证

IDEA 会自动验证配置的类型和格式：

```yaml
tony:
  wrench:
    config:
      system: user-service        # ✅ 字符串类型
      enabled: true              # ✅ 布尔类型
      refresh-interval: 5000     # ✅ 数字类型
      # refresh-interval: abc    # ❌ 类型错误，IDEA会提示
```

## 最佳实践

### 1. 系统命名规范

```yaml
# 推荐：使用有意义的系统名称
tony:
  wrench:
    config:
      system: user-service        # ✅ 用户服务
      system: order-service       # ✅ 订单服务
      system: payment-service     # ✅ 支付服务

# 不推荐：使用无意义的名称
tony:
  wrench:
    config:
      system: app1               # ❌ 无意义
      system: service            # ❌ 太通用
```

### 2. 配置分层

```yaml
# 开发环境
tony:
  wrench:
    config:
      system: user-service-dev
      refresh-interval: 1000     # 开发环境刷新更快

# 生产环境
tony:
  wrench:
    config:
      system: user-service-prod
      refresh-interval: 10000    # 生产环境刷新较慢
```

### 3. 配置验证

```java
@Component
public class ConfigValidator {
    
    @PostConstruct
    public void validateConfig() {
        // 验证必要的配置
        if (configProperties.getSystem().equals("default-system")) {
            throw new IllegalStateException("请配置 tony.wrench.config.system");
        }
    }
}
```

## 故障排除

### 1. 自动补全不工作

**问题**: IDEA 中无法自动补全配置属性

**解决方案**:
1. 检查是否包含 `spring-boot-configuration-processor` 依赖
2. 执行 `mvn clean compile` 重新编译
3. 重启 IDEA
4. 检查 `META-INF/spring-configuration-metadata.json` 文件是否生成

### 2. 配置不生效

**问题**: 配置属性值不正确

**解决方案**:
1. 检查配置文件位置是否正确
2. 验证配置前缀是否为 `tony.wrench.config`
3. 检查是否有其他配置文件覆盖了配置

### 3. 编译错误

**问题**: 编译时出现配置相关错误

**解决方案**:
1. 检查配置属性类的注解是否正确
2. 验证自动配置类是否正确注册
3. 检查 `spring.factories` 文件内容

## 版本历史

- **0.0.1-SNAPSHOT**: 初始版本，支持基础动态配置功能

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

Apache License 2.0 