# xfg-wrench-starter-design-framework 模块 pom.xml 打包与 mainClass 说明

---

## 1. 为什么这个模块的 pom.xml 没有 build/plugins？

### 1.1 依赖于父工程的 build 配置
- 你的 `xfg-wrench-starter-design-framework/pom.xml` 继承自父工程 `xfg-wrench`。
- 很多公司/开源项目会把通用的 build/plugins 配置（比如 `maven-compiler-plugin`、`maven-jar-plugin`、`maven-surefire-plugin` 等）统一写在父 pom 里，子模块只需继承即可。
- 你当前这个模块的 pom.xml 没有 `<build>`，说明它**完全继承父工程的构建行为**。

### 1.2 没有 build/plugins 也能打包
- Maven 默认会用 `maven-jar-plugin` 打包 jar（只要 `<packaging>jar</packaging>`，默认就是 jar）。
- 只要你执行 `mvn install` 或 `mvn package`，就会在 `target/` 目录下生成 jar 文件。
- 只有需要自定义行为（如 shade、assembly、指定 mainClass、资源过滤等）才需要在 `<build>` 里显式声明 plugin。

---

## 2. 这个模块能被其他项目依赖吗？

**能！**
- 只要你执行 `mvn install`，就会生成 `xfg-wrench-starter-design-framework-xxx.jar`。
- 其他项目只要在 `<dependency>` 里引用它的 groupId/artifactId/version 就能用。

---

## 3. mainClass 必须指定吗？

### 3.1 普通 jar（非可执行 jar）
- 如果只是作为**依赖库**（framework、starter），**不需要指定 mainClass**。
- 这种 jar 只是被别的项目依赖，不会直接运行。

### 3.2 可执行 jar（Spring Boot 应用/独立程序）
- 如果你希望这个模块**可以直接 java -jar 运行**，就需要：
  - 配置 `spring-boot-maven-plugin` 或 `maven-jar-plugin` 的 `<mainClass>`。
  - 这样会在 jar 的 MANIFEST.MF 里写入 Main-Class 信息。

#### 示例（Spring Boot 项目）：
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <configuration>
        <mainClass>cn.bugstack.wrench.Application</mainClass>
      </configuration>
    </plugin>
  </plugins>
</build>
```

#### 示例（普通 Java 项目）：
```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-jar-plugin</artifactId>
      <configuration>
        <archive>
          <manifest>
            <mainClass>cn.bugstack.wrench.Application</mainClass>
          </manifest>
        </archive>
      </configuration>
    </plugin>
  </plugins>
</build>
```

---

## 4. 你的 starter 设计建议

- **starter**/framework 通常不需要 mainClass，也不需要 spring-boot-maven-plugin。
- 只要能被依赖、能被 Spring 扫描到自动配置类即可。
- 如果你想让它能直接运行（比如做 demo），可以加 mainClass 和 build 插件。

---

## 5. 总结

- **没有 build/plugins 是因为父 pom 已经定义或用默认行为即可。**
- **只做依赖库，不需要 mainClass。**
- **要做可执行 jar，才需要 mainClass 和相关插件。**
- **只要 install 后有 jar，其他项目就能依赖。**

如需进一步自定义打包行为或做可执行 jar，可以补充 build/plugins 配置。 