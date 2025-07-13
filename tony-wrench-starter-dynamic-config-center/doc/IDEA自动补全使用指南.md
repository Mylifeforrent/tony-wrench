# IDEA 自动补全使用指南

## 概述

本指南详细说明如何让 IDEA 识别 `tony-wrench-starter-dynamic-config-center` 的配置属性，并在其他模块中使用时提供自动补全功能。

## 前置条件

### 1. 确保依赖配置正确

在 `tony-wrench-starter-dynamic-config-center` 的 `pom.xml` 中必须包含：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

### 2. 编译生成元数据

```bash
# 在 tony-wrench-starter-dynamic-config-center 目录下执行
mvn clean compile
```

编译后会在 `target/classes/META-INF/` 目录下生成：
- `spring-configuration-metadata.json` - 配置元数据文件
- `spring.factories` - 自动配置文件

## 在其他模块中使用

### 1. 添加依赖

在其他模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.study.tony.wrench</groupId>
    <artifactId>tony-wrench-starter-dynamic-config-center</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 配置自动补全

#### 方法一：安装到本地仓库（推荐）

```bash
# 在 tony-wrench-starter-dynamic-config-center 目录下执行
mvn clean install
```

这样其他模块就能通过 Maven 依赖找到配置元数据。

#### 方法二：IDE 项目依赖

如果使用多模块项目，确保 IDEA 能够识别模块间的依赖关系。

### 3. 重启 IDEA

安装依赖后，重启 IDEA 以确保配置元数据被正确加载。

## 验证自动补全

### 1. 创建配置文件

在其他模块的 `src/main/resources/` 目录下创建 `application.yml`：

```yaml
tony:
  wrench:
    config:
      # 在这里输入时，IDEA 应该提供自动补全
```

### 2. 测试自动补全

在 `tony.wrench.config.` 后面输入时，IDEA 应该显示以下选项：

- `system` - 系统名称
- `enabled` - 是否启用
- `refresh-interval` - 刷新间隔
- `key-prefix` - 键前缀
- `register` - 注册配置组

### 3. 测试类型检查

```yaml
tony:
  wrench:
    config:
      system: user-service        # ✅ 字符串类型
      enabled: true              # ✅ 布尔类型
      refresh-interval: 5000     # ✅ 数字类型
      # refresh-interval: abc    # ❌ 类型错误，IDEA会提示
```

## 配置属性详解

### 核心配置

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `tony.wrench.config.system` | String | `default-system` | 系统名称 |
| `tony.wrench.config.enabled` | boolean | `true` | 是否启用 |
| `tony.wrench.config.refresh-interval` | long | `5000` | 刷新间隔（毫秒） |
| `tony.wrench.config.key-prefix` | String | `tony_wrench_config` | Redis键前缀 |

### 注册配置

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `tony.wrench.config.register.host` | String | - | Redis服务器地址 |
| `tony.wrench.config.register.port` | int | `0` | Redis服务器端口 |
| `tony.wrench.config.register.password` | String | - | Redis密码 |
| `tony.wrench.config.register.pool-size` | int | `64` | 连接池大小 |
| `tony.wrench.config.register.min-idle-size` | int | `10` | 最小空闲连接数 |
| `tony.wrench.config.register.connect-timeout` | int | `10000` | 连接超时时间 |
| `tony.wrench.config.register.idle-timeout` | int | `10000` | 空闲超时时间 |
| `tony.wrench.config.register.retry-attempts` | int | `3` | 重试次数 |
| `tony.wrench.config.register.retry-interval` | int | `1000` | 重试间隔 |
| `tony.wrench.config.register.keep-alive` | boolean | `true` | 是否保持长连接 |
| `tony.wrench.config.register.ping-interval` | int | `0` | 心跳间隔 |

## 使用示例

### 1. 基础配置

```yaml
tony:
  wrench:
    config:
      system: user-service
      enabled: true
```

### 2. 完整配置

```yaml
tony:
  wrench:
    config:
      system: user-service
      enabled: true
      refresh-interval: 5000
      key-prefix: my_config
      register:
        host: localhost
        port: 6379
        password: mypassword
        pool-size: 32
        min-idle-size: 5
        connect-timeout: 5000
        idle-timeout: 8000
        retry-attempts: 2
        retry-interval: 500
        keep-alive: true
        ping-interval: 1000
```

### 3. 环境特定配置

```yaml
# application-dev.yml
tony:
  wrench:
    config:
      system: user-service-dev
      refresh-interval: 1000

# application-prod.yml
tony:
  wrench:
    config:
      system: user-service-prod
      refresh-interval: 10000
```

## 故障排除

### 1. 自动补全不工作

**检查步骤**：
1. 确认依赖已正确添加
2. 确认已执行 `mvn clean install`
3. 重启 IDEA
4. 检查 `target/classes/META-INF/spring-configuration-metadata.json` 是否存在

**解决方案**：
```bash
# 重新编译和安装
cd tony-wrench-starter-dynamic-config-center
mvn clean install

# 在其他模块中重新导入依赖
cd ../other-module
mvn clean compile
```

### 2. 配置不生效

**检查步骤**：
1. 确认配置前缀正确：`tony.wrench.config`
2. 确认配置文件位置正确
3. 检查是否有其他配置文件覆盖

**解决方案**：
```yaml
# 确保配置前缀正确
tony:
  wrench:
    config:  # 注意这里的层级
      system: user-service
```

### 3. 类型检查错误

**检查步骤**：
1. 确认配置属性类型正确
2. 检查默认值设置

**解决方案**：
```yaml
tony:
  wrench:
    config:
      system: user-service        # 字符串
      enabled: true              # 布尔值
      refresh-interval: 5000     # 数字
      key-prefix: my_prefix      # 字符串
```

## 最佳实践

### 1. 配置验证

```java
@Component
public class ConfigValidator {
    
    @Autowired
    private DynamicConfigCenterAutoProperties properties;
    
    @PostConstruct
    public void validateConfig() {
        if ("default-system".equals(properties.getSystem())) {
            throw new IllegalStateException("请配置 tony.wrench.config.system");
        }
    }
}
```

### 2. 配置文档

在项目中添加配置说明文档，帮助其他开发者理解配置项的作用。

### 3. 测试配置

创建测试配置文件，验证配置的正确性：

```yaml
# application-test.yml
tony:
  wrench:
    config:
      system: test-service
      enabled: true
      refresh-interval: 1000
```

## 总结

通过以上步骤，其他模块在使用 `tony-wrench-starter-dynamic-config-center` 时，IDEA 将能够：

1. ✅ 提供配置属性的自动补全
2. ✅ 进行配置类型检查
3. ✅ 显示配置属性的描述信息
4. ✅ 显示配置属性的默认值
5. ✅ 提供配置验证和错误提示

这大大提高了开发效率和配置的准确性。 