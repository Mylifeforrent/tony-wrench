# Spring Boot 插件配置说明

## 问题背景

在 `tony-wrench-starter-design-framework` 模块执行 `mvn install` 时出现以下错误：

```
[ERROR] Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:30.5package (repackage) on project tony-wrench-starter-design-framework: Execution repackage of goal org.springframework.boot:spring-boot-maven-plugin:3.5.3:repackage failed: Unable to find main class
```

## 错误原因分析

### 1Starter 模块的特点
- **Starter 模块**：提供功能组件，不启动 Web 服务
- **没有主类**：不应该有 `@SpringBootApplication` 注解的类
- **作为依赖**：被其他应用作为 jar 包依赖使用

###2 Spring Boot 插件的行为
- **默认行为**：`spring-boot-maven-plugin` 会执行 `repackage` 目标
- **repackage 目标**：创建可执行的 jar 包，需要主类
- **主类查找**：自动查找带有 `@SpringBootApplication` 的类
- **查找失败**：Starter 模块没有主类，导致构建失败

## 解决方案

### 方案一：跳过 Spring Boot 打包（推荐）

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
```

**优点**：
- 保留插件，便于后续配置
- 跳过 repackage 目标，避免主类查找
- 仍然可以打包成普通的 jar

### 方案二：完全移除插件

```xml
<!-- 如果不需要 Spring Boot 插件的任何功能，可以直接移除 -->
```

**适用场景**：
- 纯工具类模块
- 不需要 Spring Boot 特定功能
- 只需要普通的 jar 打包

## 配置对比

### 修改前（有问题）
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

### 修改后（推荐）
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
```

## 最佳实践

### 1. Starter 模块配置
```xml
<!-- Starter 模块：跳过 repackage -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
```

###2 Web 应用模块配置
```xml
<!-- Web 应用模块：正常配置 -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.example.Application</mainClass>
    </configuration>
</plugin>
```

###3工具模块配置
```xml
<!-- 工具模块：可以完全移除插件 -->
<!-- 不需要 Spring Boot 插件 -->
```

## 验证修复

修复后，重新执行构建：

```bash
mvn clean install
```

应该能够成功构建，不再出现 "Unable to find main class错误。

## 总结

- **Starter 模块**：使用 `<skip>true</skip>` 跳过 repackage
- **Web 应用模块**：正常配置主类
- **工具模块**：可以移除插件

这样既保证了 Starter 模块的正确构建，又避免了不必要的配置复杂性。 