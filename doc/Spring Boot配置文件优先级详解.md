# Spring Boot 配置文件优先级详解

## 1. 配置文件类型

### 1.1 支持的配置文件格式
Spring Boot 支持多种配置文件格式：

```
application.yml          # YAML 格式
application.yaml         # YAML 格式（完整扩展名）
application.properties   # Properties 格式
bootstrap.yml           # Bootstrap YAML 格式
bootstrap.yaml          # Bootstrap YAML 格式（完整扩展名）
bootstrap.properties    # Bootstrap Properties 格式
```

### 1.2 配置文件位置
```
src/main/resources/
├── application.yml
├── application.properties
├── bootstrap.yml
├── bootstrap.properties
└── config/
    ├── application.yml
    ├── application.properties
    ├── bootstrap.yml
    └── bootstrap.properties
```

## 2. 配置文件优先级

### 2.1 总体优先级顺序（从高到低）

```
1. 命令行参数 (--spring.config.location)
2. Java 系统属性 (System.getProperties())
3. 操作系统环境变量
4. 配置文件（按优先级排序）
   ├── file:./config/
   ├── file:./
   ├── classpath:/config/
   └── classpath:/
```

### 2.2 配置文件内部优先级

#### 2.2.1 同一格式的优先级
```
# 从高到低
1. application-{profile}.yml
2. application-{profile}.properties
3. application.yml
4. application.properties
```

#### 2.2.2 不同格式的优先级
```
# 同一位置，不同格式的优先级（从高到低）
1. .yml 格式
2. .yaml 格式
3. .properties 格式
```

### 2.3 Bootstrap vs Application 优先级

#### 2.3.1 Bootstrap 配置特点
- **加载时机**: 在 Application Context 创建之前加载
- **用途**: 配置外部化配置源（如 Config Server）
- **优先级**: Bootstrap 配置会被 Application 配置覆盖

#### 2.3.2 具体优先级
```
# 从高到低
1. application.yml
2. application.properties
3. bootstrap.yml
4. bootstrap.properties
```

## 3. 配置加载机制

### 3.1 配置合并策略

#### 3.1.1 不同配置项
如果三个配置文件存在不同的配置项，**所有配置项都会被加载**：

```yaml
# bootstrap.yml
spring:
  application:
    name: my-app
  cloud:
    config:
      uri: http://config-server:8888

# application.yml
server:
  port: 8080
logging:
  level: INFO

# application.properties
my.custom.property=value
```

**结果**: 所有配置都会被加载，没有冲突。

#### 3.1.2 相同配置项
如果存在相同的配置项，**高优先级的配置会覆盖低优先级的配置**：

```yaml
# bootstrap.yml
server:
  port: 8081
spring:
  application:
    name: my-app

# application.yml
server:
  port: 8080  # 这个会覆盖 bootstrap.yml 中的 8081
logging:
  level: INFO

# application.properties
server.port=8082  # 这个会覆盖 application.yml 中的 8080
```

**结果**: 最终 `server.port` 的值为 `8082`。

### 3.2 配置加载顺序

#### 3.2.1 启动阶段
```
1. 加载 bootstrap 配置
   ├── bootstrap.yml
   ├── bootstrap.yaml
   └── bootstrap.properties

2. 创建 Bootstrap Application Context

3. 加载 application 配置
   ├── application.yml
   ├── application.yaml
   └── application.properties

4. 创建 Application Context
```

#### 3.2.2 配置覆盖机制
```
Bootstrap 配置 → Application 配置 → 环境变量 → 系统属性 → 命令行参数
     ↓              ↓              ↓           ↓           ↓
   低优先级 ←→ 高优先级
```

## 4. 实际示例

### 4.1 示例 1: 不同配置项

#### 配置文件内容
```yaml
# bootstrap.yml
spring:
  application:
    name: my-service
  cloud:
    config:
      uri: http://config-server:8888
      fail-fast: true

# application.yml
server:
  port: 8080
logging:
  level:
    root: INFO
    com.example: DEBUG

# application.properties
my.feature.enabled=true
my.custom.timeout=5000
```

#### 加载结果
```java
// 所有配置都会被加载
@Value("${spring.application.name}")
private String appName; // "my-service"

@Value("${server.port}")
private int port; // 8080

@Value("${my.feature.enabled}")
private boolean featureEnabled; // true

@Value("${spring.cloud.config.uri}")
private String configUri; // "http://config-server:8888"
```

### 4.2 示例 2: 相同配置项

#### 配置文件内容
```yaml
# bootstrap.yml
server:
  port: 8081
spring:
  application:
    name: bootstrap-app

# application.yml
server:
  port: 8080  # 覆盖 bootstrap.yml 的 8081
spring:
  application:
    name: application-app  # 覆盖 bootstrap.yml 的 bootstrap-app

# application.properties
server.port=8082  # 覆盖 application.yml 的 8080
spring.application.name=properties-app  # 覆盖 application.yml 的 application-app
```

#### 加载结果
```java
@Value("${server.port}")
private int port; // 8082 (来自 application.properties)

@Value("${spring.application.name}")
private String appName; // "properties-app" (来自 application.properties)
```

## 5. 最佳实践

### 5.1 配置文件使用建议

#### 5.1.1 Bootstrap 配置
```yaml
# bootstrap.yml - 用于外部化配置
spring:
  application:
    name: my-service
  cloud:
    config:
      uri: http://config-server:8888
      fail-fast: true
    discovery:
      enabled: true
```

#### 5.1.2 Application 配置
```yaml
# application.yml - 应用级配置
server:
  port: 8080
logging:
  level:
    root: INFO
    com.example: DEBUG

# 业务配置
my:
  service:
    timeout: 5000
    retry-count: 3
```

#### 5.1.3 Properties 配置
```properties
# application.properties - 环境特定配置
# 通常用于覆盖默认值
server.port=9090
logging.level.com.example=TRACE
my.service.timeout=10000
```

### 5.2 配置分层策略

#### 5.2.1 分层原则
```
Bootstrap 配置: 外部化配置源、服务发现、配置中心
Application 配置: 应用级配置、业务配置
Properties 配置: 环境特定配置、运行时覆盖
```

#### 5.2.2 环境配置
```yaml
# application-dev.yml
server:
  port: 8080
logging:
  level: DEBUG

# application-prod.yml
server:
  port: 80
logging:
  level: WARN
```

### 5.3 配置验证

#### 5.3.1 查看加载的配置
```java
@SpringBootApplication
public class MyApplication {
    
    @Autowired
    private Environment environment;
    
    @PostConstruct
    public void printConfig() {
        System.out.println("Server Port: " + environment.getProperty("server.port"));
        System.out.println("App Name: " + environment.getProperty("spring.application.name"));
    }
}
```

#### 5.3.2 使用配置属性类
```java
@ConfigurationProperties(prefix = "my.service")
@Data
public class MyServiceProperties {
    private int timeout = 5000;
    private int retryCount = 3;
    private boolean enabled = true;
}
```

## 6. 常见问题

### 6.1 配置不生效

#### 6.1.1 检查优先级
```bash
# 查看实际加载的配置
java -jar myapp.jar --debug

# 或者使用 Spring Boot Actuator
curl http://localhost:8080/actuator/env
```

#### 6.1.2 检查文件位置
```bash
# 确保配置文件在正确位置
ls -la src/main/resources/
# 应该看到 application.yml, bootstrap.yml 等文件
```

### 6.2 配置冲突

#### 6.2.1 使用 @ConfigurationProperties
```java
// 避免使用 @Value，改用配置属性类
@ConfigurationProperties(prefix = "my.config")
@Data
public class MyConfig {
    private String property1;
    private int property2;
}
```

#### 6.2.2 明确配置来源
```yaml
# 在配置文件中添加注释说明配置来源
# 这个配置会被 application.properties 覆盖
server:
  port: 8080
```

## 7. 总结

### 7.1 优先级总结

1. **格式优先级**: YAML > Properties
2. **文件优先级**: Application > Bootstrap
3. **位置优先级**: 外部 > 内部
4. **环境优先级**: 命令行 > 环境变量 > 配置文件

### 7.2 加载机制

- **不同配置项**: 全部加载，无冲突
- **相同配置项**: 高优先级覆盖低优先级
- **配置合并**: 支持配置的继承和覆盖

### 7.3 最佳实践

1. **Bootstrap**: 用于外部化配置源
2. **Application**: 用于应用级配置
3. **Properties**: 用于环境特定配置
4. **分层管理**: 按职责分离配置
5. **文档说明**: 明确配置来源和用途

通过理解这些优先级和加载机制，可以更好地管理 Spring Boot 应用的配置。 