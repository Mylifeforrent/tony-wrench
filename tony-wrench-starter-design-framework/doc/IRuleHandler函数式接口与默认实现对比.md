# IRuleHandler 函数式接口与默认实现方式对比

## 1. IRuleHandler 是否为函数式接口？

### 你的接口定义：
```java
public interface IRuleHandler<I, D, R> {
    R apply(I inputParams, D dynamicContext) throws Exception;

    IRuleHandler DEFAULT = new DefaultRuleHandler();
    StrategyHandler DEFAULT = (T, D) -> null;
}
```

### 判断标准
- **函数式接口**：必须有且只有一个抽象方法（可以有默认方法、静态方法、常量等）。
- **@FunctionalInterface** 注解不是必须的，但加上更规范。

### 你的接口分析
- 只有一个抽象方法 `R apply(I inputParams, D dynamicContext) throws Exception;`
- 其余都是常量（静态字段），不影响函数式接口的定义。

**结论：**
- 结构上**可以**作为函数式接口使用（即可以用 Lambda 表达式赋值）。
- 但**没有加 @FunctionalInterface 注解**，建议加上以明确语义和防止误用。

#### 推荐写法：
```java
@FunctionalInterface
public interface IRuleHandler<I, D, R> {
    R apply(I inputParams, D dynamicContext) throws Exception;

    IRuleHandler DEFAULT = new DefaultRuleHandler();
}
```

---

## 2. 两种默认实现方式对比

### 方式一：实现类实例
```java
IRuleHandler DEFAULT = new DefaultRuleHandler();
```
- 需要有一个具体的实现类 `DefaultRuleHandler`。
- 适合有复杂逻辑、需要多方法实现的默认实现。
- 兼容 Java 8 之前的写法。
- 适合需要复用、逻辑复杂的场景。

### 方式二：Lambda 表达式
```java
StrategyHandler DEFAULT = (T, D) -> null;
```
- 只适用于**函数式接口**（只有一个抽象方法）。
- 代码简洁，适合简单的默认实现（如返回 null、空集合等）。
- 不能有复杂逻辑或多方法实现。
- 需要 Java 8 及以上。

---

## 3. 对比总结表

| 方式                | 适用接口类型     | 代码风格 | 复杂逻辑 | 兼容性 | 推荐场景         |
|---------------------|-----------------|----------|----------|--------|------------------|
| 实现类实例          | 任意接口        | 传统      | 支持     | 高     | 复杂默认实现     |
| Lambda 表达式       | 函数式接口      | 现代      | 不支持   | Java8+ | 简单默认实现     |

---

## 4. 推荐建议

- **如果只是简单的默认实现（如返回 null/空），且接口是函数式接口，推荐用 Lambda。**
- **如果默认实现有复杂逻辑，或接口不是函数式接口，推荐用实现类实例。**
- **建议为函数式接口加上 @FunctionalInterface 注解，语义更明确、更安全。**

---

如需具体代码示例或重构建议，欢迎继续提问！ 