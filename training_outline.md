### Spring Boot JPA 核心技术培训大纲

#### 1. 培训目标

*   理解 JPA 的核心概念、优势以及与其他 ORM 框架（MyBatis/Hibernate）的对比。
*   熟练掌握 Spring Boot JPA 的基础用法和核心 API。
*   掌握各种复杂查询技巧，包括多表关联、动态条件、分页、原生 SQL 等。
*   了解并实践 JSON/JSONB 类型字段的处理方式。
*   掌握使用 HQL/JPQL 查询并返回 DTO 的方法。
*   能够独立在项目中使用 Spring Boot JPA 解决常见的数据库操作问题。

#### 2. 培训对象

*   具有一定 Java/Kotlin 基础的初、中级开发工程师。
*   对 Spring Boot 有基本了解。

#### 3. 培训内容大纲

##### 第一部分：JPA 核心概念与优势 (1.5h)

#### 1.1 JPA 是什么？

**1.1.1 JPA (Java Persistence API) 规范介绍**
*   **定义：** JPA 是 Java EE 规范的一部分，为 Java 应用程序提供了对象关系映射 (ORM) 的标准化 API
*   **发展历程：**
    *   JPA 1.0 (2006年) - JSR 220，基础 ORM 功能
    *   JPA 2.0 (2009年) - JSR 317，增加 Criteria API、乐观锁等
    *   JPA 2.1 (2013年) - JSR 338，支持 Stored Procedures、Entity Graphs
    *   JPA 2.2 (2017年) - 流式查询、CDI 支持
    *   JPA 3.0 (2020年) - Jakarta EE 的一部分，命名空间变更
*   **核心价值：** 提供统一的、标准化的 Java 持久化解决方案

**1.1.2 ORM (Object-Relational Mapping) 思想解析**
*   **核心概念：** 解决面向对象编程与关系型数据库之间的"阻抗不匹配"问题
*   **映射关系：**
    ```
    Java 对象          ←→  数据库表
    ┌─────────────┐        ┌─────────────┐
    │   User      │   ←→   │   users     │
    │ - id: Long  │        │ - id: BIGINT│
    │ - name: Str │        │ - name: TEXT│
    │ - orders    │        │             │
    └─────────────┘        └─────────────┘
           │                      │
           ↓                      ↓ 
    List<Order>            orders 表 (外键关联)
    ```
*   **ORM 解决的核心问题：**
    *   **类型映射：** Java 基本类型与数据库字段类型的自动转换
    *   **关联关系：** 对象引用与外键约束的映射
    *   **继承映射：** 面向对象继承与关系型表结构的映射
    *   **查询抽象：** 面向对象的查询语言 (JPQL) 与 SQL 的转换

**1.1.3 JPA 与 Hibernate, EclipseLink 等实现的关系**
*   **规范与实现的关系：**
    ```
         JPA 规范 (接口定义)
              ├── Hibernate (最流行的实现)
              ├── EclipseLink (Eclipse 基金会)
              ├── OpenJPA (Apache 项目)
              └── DataNucleus (开源实现)
    ```
*   **Hibernate 作为主流实现：**
    *   市场占有率最高 (~80%)
    *   Spring Boot 默认集成
    *   功能最完善，生态最丰富
    *   向后兼容性好
*   **实际开发中的意义：**
    *   编程时使用 JPA 标准注解和 API
    *   运行时依赖具体实现 (通常是 Hibernate)
    *   可以在不同实现之间切换 (理论上)

#### 1.2 为什么选择 JPA？—— 与主流 ORM 框架对比

**1.2.1 JPA vs. MyBatis 深度对比**

| 对比维度 | JPA | MyBatis |
|---------|-----|---------|
| **学习曲线** | 陡峭，需要理解 ORM 概念 | 平缓，接近原生 SQL |
| **开发效率** | 高，自动生成 CRUD | 中等，需要手写 SQL |
| **SQL 控制** | 有限，主要通过 JPQL/原生 SQL | 完全控制，手写所有 SQL |
| **类型安全** | 强类型，编译期检查 | 弱类型，运行期才发现错误 |
| **重构支持** | 优秀，IDE 支持良好 | 有限，需要手动修改 SQL |
| **复杂查询** | 中等，复杂查询需要原生 SQL | 优秀，可以写任意复杂的 SQL |
| **性能优化** | 自动优化，但需要了解机制 | 手动优化，控制力强 |
| **数据库无关性** | 好，JPQL 自动适配 | 差，SQL 方言需要处理 |

**开发效率对比示例：**

*JPA 实现用户 CRUD：*
```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;
    // getters, setters
}

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByNameContaining(String name);
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}
// 无需额外配置，自动提供 save(), findById(), delete() 等方法
```

*MyBatis 实现相同功能：*
```java
public class User {
    private Long id;
    private String name; 
    private String email;
    // getters, setters
}

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);
    
    @Update("UPDATE users SET name=#{name}, email=#{email} WHERE id=#{id}")
    void update(User user);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    void delete(@Param("id") Long id);
    
    @Select("SELECT * FROM users WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<User> findByNameContaining(@Param("name") String name);
    
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
}
```

**类型安全与重构支持对比：**
```java
// JPA: 类型安全的查询
public List<User> findActiveUsers() {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> query = cb.createQuery(User.class);
    Root<User> root = query.from(User.class);
    query.where(cb.equal(root.get("status"), UserStatus.ACTIVE)); // 编译期检查
    return entityManager.createQuery(query).getResultList();
}

// MyBatis: 字符串拼接，运行期才发现错误
@Select("SELECT * FROM users WHERE status = #{status}")
List<User> findActiveUsers(@Param("status") String status); // 字段名拼写错误只能运行时发现
```

**1.2.2 JPA vs. Hibernate 关系澄清**

*很多开发者混淆 JPA 和 Hibernate 的关系，这里澄清：*

**规范与实现的关系：**
```java
// 这是 JPA 规范的代码（标准化）
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

// 这是 Hibernate 特有的功能（实现扩展）
@Entity
@Table(name = "users")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @org.hibernate.annotations.Type(type = "json")
    private Map<String, Object> metadata; // Hibernate 特有的 JSON 类型
}
```

**API 标准化的优势：**
*   **可移植性：** 理论上可以在不同 JPA 实现间切换
*   **学习成本：** 掌握 JPA 规范，适用于所有实现
*   **团队协作：** 统一的 API 标准，减少学习成本
*   **长期维护：** 规范相对稳定，不受厂商策略影响

#### 1.3 Spring Data JPA 介绍

**1.3.1 Spring Data JPA 的核心理念：简化数据访问层开发**

*Spring Data JPA 是 Spring Data 项目的一部分，它在 JPA 规范之上提供了更高层次的抽象：*

**层次关系：**
```
Spring Data JPA (高级抽象)
       ↓
JPA 规范 (标准化接口)
       ↓  
Hibernate (具体实现)
       ↓
JDBC (底层数据库访问)
```

**核心设计理念：**
*   **约定优于配置：** 通过方法名约定自动生成查询
*   **样板代码消除：** 提供通用的 CRUD 操作接口
*   **声明式编程：** 通过注解和接口定义数据访问逻辑
*   **类型安全：** 强类型的 Repository 接口

**1.3.2 Repository 抽象的核心优势**

**Repository 接口层次结构：**
```java
Repository<T, ID>                    // 标记接口
    ↓
CrudRepository<T, ID>               // 基础 CRUD 操作
    ↓
PagingAndSortingRepository<T, ID>   // 分页和排序
    ↓
JpaRepository<T, ID>                // JPA 特有功能
```

**每层提供的功能：**

*CrudRepository:*
```java
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    <S extends T> S save(S entity);           // 保存实体
    Optional<T> findById(ID id);              // 根据 ID 查找
    boolean existsById(ID id);                // 判断是否存在
    Iterable<T> findAll();                    // 查找所有
    long count();                             // 统计数量
    void deleteById(ID id);                   // 根据 ID 删除
    void delete(T entity);                    // 删除实体
    void deleteAll();                         // 删除所有
}
```

*JpaRepository 额外功能:*
```java
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
    List<T> findAll();                        // 返回 List 而非 Iterable
    List<T> findAll(Sort sort);               // 排序查询
    List<T> findAllById(Iterable<ID> ids);    // 批量查询
    <S extends T> List<S> saveAll(Iterable<S> entities); // 批量保存
    void flush();                             // 强制同步到数据库
    <S extends T> S saveAndFlush(S entity);   // 保存并刷新
    void deleteInBatch(Iterable<T> entities); // 批量删除
    T getOne(ID id);                          // 获取懒加载代理
}
```

**实际开发中的优势体现：**

*传统 DAO 模式 vs Repository 模式:*
```java
// 传统 DAO 实现
@Repository
public class UserDAOImpl implements UserDAO {
    @PersistenceContext
    private EntityManager entityManager;
    
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }
    
    public Optional<User> findById(Long id) {
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }
    
    public List<User> findByName(String name) {
        return entityManager.createQuery(
            "SELECT u FROM User u WHERE u.name = :name", User.class)
            .setParameter("name", name)
            .getResultList();
    }
    // ... 更多样板代码
}

// Spring Data JPA Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);  // 仅需要方法签名，自动实现
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}
```

**优势总结：**
1. **代码量减少 80%：** 无需实现基础 CRUD 操作
2. **自动查询生成：** 根据方法名自动生成查询逻辑
3. **类型安全：** 编译期检查，减少运行时错误
4. **测试友好：** 易于单元测试和集成测试
5. **统一标准：** 团队内部统一的数据访问模式

##### 第二部分：Spring Boot JPA 基础实践 (2.5h)

#### 2.1 环境准备与项目初始化

**2.1.1 使用 Spring Initializr 创建项目**

*访问 https://start.spring.io/ 或使用 IDE 创建项目：*

**项目配置：**
```
Project: Maven/Gradle
Language: Java 17 / Kotlin
Spring Boot: 3.2.x (最新稳定版)
Group: com.example
Artifact: jpa-demo
Name: jpa-demo
Description: Spring Boot JPA Demo Project
Package name: com.example.jpademo
Packaging: Jar
Java: 17
```

**必需依赖选择：**
- Spring Web (构建 REST API)
- Spring Data JPA (JPA 支持)
- H2 Database (内存数据库，便于演示)
- PostgreSQL Driver (生产环境数据库)
- Spring Boot DevTools (开发工具)
- Validation (数据验证)

**2.1.2 Maven 依赖配置详解**

*生成的 pom.xml 核心部分：*
```xml
<dependencies>
    <!-- Spring Boot Web Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Boot JPA Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- H2 数据库 (开发/测试环境) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- PostgreSQL 驱动 (生产环境) -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- 数据验证 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

**spring-boot-starter-data-jpa 内部包含：**
- spring-data-jpa (Spring Data JPA 核心)
- hibernate-core (JPA 实现)
- spring-orm (Spring ORM 支持)
- jakarta.persistence-api (JPA 规范 API)
- spring-tx (事务支持)

**2.1.3 数据库配置**

*application.yml 完整配置示例：*
```yaml
# 开发环境配置 (H2 内存数据库)
spring:
  profiles:
    active: dev
  
---
# 开发环境
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true  # 启用 H2 控制台，访问 http://localhost:8080/h2-console
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop  # 每次启动重新创建表
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true

---
# 生产环境
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://localhost:5432/jpademo
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME:jpauser}
    password: ${DB_PASSWORD:jpapass}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate  # 生产环境只验证表结构，不自动创建
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        jdbc:
          batch_size: 25
        order_inserts: true
        order_updates: true
```

#### 2.2 核心注解与实体映射

**2.2.1 基础实体注解详解**

*创建第一个实体类：*
```java
package com.example.jpademo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity  // 标记为 JPA 实体
@Table(name = "users",  // 指定数据库表名
       indexes = {
           @Index(name = "idx_user_email", columnList = "email", unique = true),
           @Index(name = "idx_user_username", columnList = "username")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_email", columnNames = "email")
       })
public class User {
    
    @Id  // 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增主键
    @Column(name = "id")
    private Long id;
    
    @Column(name = "username", 
            nullable = false,      // 不允许为空
            length = 50,          // 最大长度
            unique = true)        // 唯一约束
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;
    
    @Column(name = "email", nullable = false, length = 100)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Column(name = "age")
    @Min(value = 0, message = "年龄不能为负数")
    @Max(value = 150, message = "年龄不能超过150")
    private Integer age;
    
    @Column(name = "salary", precision = 10, scale = 2)  // 精度控制
    private BigDecimal salary;
    
    @Enumerated(EnumType.STRING)  // 枚举存储为字符串
    @Column(name = "status", length = 20)
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "is_active")
    private Boolean active = true;
    
    // 审计字段
    @CreationTimestamp  // Hibernate 注解，自动设置创建时间
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp   // 自动设置更新时间
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 不持久化字段
    @Transient
    private String fullDisplayName;
    
    // 构造函数
    public User() {}
    
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // ... 其他 getter/setter
    
    // 业务方法
    @PostLoad  // 实体加载后执行
    public void calculateDisplayName() {
        this.fullDisplayName = this.username + " (" + this.email + ")";
    }
    
    @PrePersist  // 持久化前执行
    public void prePersist() {
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
    }
    
    @PreUpdate  // 更新前执行
    public void preUpdate() {
        // 可以在这里添加更新前的业务逻辑
    }
    
    // equals 和 hashCode (基于 ID)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}

// 枚举定义
enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, DELETED
}
```

**2.2.2 字段类型映射与最佳实践**

*JPA 字段类型映射表：*

| Java 类型 | 数据库类型 (PostgreSQL) | 数据库类型 (MySQL) | 注意事项 |
|-----------|----------------------|-------------------|----------|
| String | VARCHAR/TEXT | VARCHAR/TEXT | 使用 @Column(length=) 控制长度 |
| Integer/int | INTEGER | INT | 推荐使用包装类型以支持 null |
| Long/long | BIGINT | BIGINT | 主键推荐使用 Long |
| BigDecimal | NUMERIC(p,s) | DECIMAL(p,s) | 金额字段必须使用 |
| LocalDateTime | TIMESTAMP | DATETIME | 推荐使用 LocalDateTime |
| LocalDate | DATE | DATE | 日期字段 |
| Boolean/boolean | BOOLEAN | TINYINT(1) | 推荐使用包装类型 |
| byte[] | BYTEA | BLOB | 存储二进制数据 |
| Enum | VARCHAR | VARCHAR | 使用 @Enumerated(EnumType.STRING) |

**字段映射最佳实践：**
```java
public class BestPracticeEntity {
    
    // ✅ 好的做法
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;  // 金额字段使用 BigDecimal
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 时间字段使用 LocalDateTime
    
    @Enumerated(EnumType.STRING)  // 枚举存储为字符串，便于阅读
    @Column(name = "status", length = 20)
    private Status status;
    
    // ❌ 避免的做法
    private float price;        // float 精度问题
    private Date createdAt;     // Date 类型过时
    @Enumerated(EnumType.ORDINAL)  // 枚举存储为数字，不利于维护
    private Status status;
}
```

#### 2.3 第一个 Repository：JpaRepository 详解

**2.3.1 Repository 接口继承关系深入理解**

*创建 UserRepository：*
```java
package com.example.jpademo.repository;

import com.example.jpademo.entity.User;
import com.example.jpademo.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository  // 可选注解，但建议添加以明确语义
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 继承自 CrudRepository 的方法（自动提供）：
    // <S extends User> S save(S entity);
    // Optional<User> findById(Long id);
    // boolean existsById(Long id);
    // Iterable<User> findAll();
    // long count();
    // void deleteById(Long id);
    // void delete(User entity);
    
    // 继承自 PagingAndSortingRepository 的方法：
    // Page<User> findAll(Pageable pageable);
    // Iterable<User> findAll(Sort sort);
    
    // 继承自 JpaRepository 的方法：
    // List<User> findAll();
    // List<User> findAllById(Iterable<Long> ids);
    // <S extends User> List<S> saveAll(Iterable<S> entities);
    // void flush();
    // <S extends User> S saveAndFlush(S entity);
    // void deleteInBatch(Iterable<User> entities);
    // User getOne(Long id);  // 获取懒加载代理，注意 LazyInitializationException
    
    // 自定义查询方法（后续章节详细讲解）
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);
    List<User> findByUsernameContainingIgnoreCase(String username);
}
```

**2.3.2 CRUD 操作实践演示**

*创建 UserService 演示基础操作：*
```java
package com.example.jpademo.service;

import com.example.jpademo.entity.User;
import com.example.jpademo.entity.UserStatus;
import com.example.jpademo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // CREATE 操作示例
    public User createUser(String username, String email, Integer age) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setAge(age);
        user.setStatus(UserStatus.ACTIVE);
        user.setSalary(new BigDecimal("5000.00"));
        
        // save() 方法：如果 ID 为 null 则插入，否则更新
        return userRepository.save(user);
    }
    
    // 批量创建
    public List<User> createUsers(List<User> users) {
        // saveAll() 批量保存，比循环调用 save() 更高效
        return userRepository.saveAll(users);
    }
    
    // READ 操作示例
    @Transactional(readOnly = true)  // 只读事务，性能优化
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        // findById() 返回 Optional，需要处理不存在的情况
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<User> getAllUsersSorted() {
        // 按用户名排序
        return userRepository.findAll(Sort.by("username").ascending());
    }
    
    @Transactional(readOnly = true)
    public Page<User> getUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable);
    }
    
    // UPDATE 操作示例
    public User updateUser(Long id, String newEmail, Integer newAge) {
        User user = getUserById(id);  // 先查询
        user.setEmail(newEmail);      // 修改属性
        user.setAge(newAge);
        return userRepository.save(user);  // 保存更新
    }
    
    public User updateUserWithFlush(Long id, String newEmail) {
        User user = getUserById(id);
        user.setEmail(newEmail);
        // saveAndFlush() 立即将更改同步到数据库
        return userRepository.saveAndFlush(user);
    }
    
    // DELETE 操作示例
    public void deleteUser(Long id) {
        // 检查是否存在
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public void deleteUserEntity(User user) {
        userRepository.delete(user);  // 直接删除实体对象
    }
    
    public void deleteUsersInBatch(List<User> users) {
        // 批量删除，性能更好
        userRepository.deleteInBatch(users);
    }
    
    // 统计操作
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    @Transactional(readOnly = true)
    public boolean isUserExists(Long id) {
        return userRepository.existsById(id);
    }
}
```

**2.3.3 REST Controller 集成示例**

*创建 UserController 演示完整的 Web 集成：*
```java
package com.example.jpademo.controller;

import com.example.jpademo.entity.User;
import com.example.jpademo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // CREATE - 创建用户
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(
            request.getUsername(), 
            request.getEmail(), 
            request.getAge()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    // READ - 获取单个用户
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // READ - 获取所有用户（分页）
    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> users = userService.getUsersPaginated(page, size);
        return ResponseEntity.ok(users);
    }
    
    // UPDATE - 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateUser(id, request.getEmail(), request.getAge());
        return ResponseEntity.ok(updatedUser);
    }
    
    // DELETE - 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // 统计接口
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        long count = userService.getTotalUserCount();
        return ResponseEntity.ok(count);
    }
}

// DTO 类定义
class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Min(value = 0, message = "年龄不能为负数")
    private Integer age;
    
    // getters and setters
}

class UpdateUserRequest {
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Min(value = 0, message = "年龄不能为负数")
    private Integer age;
    
    // getters and setters
}
```

**2.3.4 测试用例编写**

*创建集成测试：*
```java
package com.example.jpademo.repository;

import com.example.jpademo.entity.User;
import com.example.jpademo.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // 自动配置测试用的 JPA 环境
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testSaveAndFindById() {
        // Given
        User user = new User("testuser", "test@example.com");
        user.setAge(25);
        
        // When
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    void testFindAll() {
        // Given
        entityManager.persist(new User("user1", "user1@example.com"));
        entityManager.persist(new User("user2", "user2@example.com"));
        entityManager.flush();
        
        // When
        Iterable<User> users = userRepository.findAll();
        
        // Then
        assertThat(users).hasSize(2);
    }
    
    @Test
    void testFindAllWithPagination() {
        // Given - 创建 15 个用户
        for (int i = 1; i <= 15; i++) {
            entityManager.persist(new User("user" + i, "user" + i + "@example.com"));
        }
        entityManager.flush();
        
        // When
        Page<User> firstPage = userRepository.findAll(PageRequest.of(0, 10));
        Page<User> secondPage = userRepository.findAll(PageRequest.of(1, 10));
        
        // Then
        assertThat(firstPage.getTotalElements()).isEqualTo(15);
        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(secondPage.isLast()).isTrue();
    }
    
    @Test
    void testDelete() {
        // Given
        User user = entityManager.persist(new User("deleteuser", "delete@example.com"));
        entityManager.flush();
        Long userId = user.getId();
        
        // When
        userRepository.deleteById(userId);
        
        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }
    
    @Test
    void testCount() {
        // Given
        entityManager.persist(new User("count1", "count1@example.com"));
        entityManager.persist(new User("count2", "count2@example.com"));
        entityManager.flush();
        
        // When
        long count = userRepository.count();
        
        // Then
        assertThat(count).isEqualTo(2);
    }
}
```

##### 第三部分：JPA 查询方法全解析 (3h)

#### 3.1 方法命名规则查询 (Query Methods)

**3.1.1 核心理念与工作原理**

*   **约定优于配置：** Spring Data JPA 通过解析 Repository 接口中符合特定规范的方法名，自动生成对应的 JPQL 查询。
*   **工作流程：**
    1.  应用启动时，Spring Data JPA 扫描所有继承 `Repository` 的接口。
    2.  解析每个方法的名称，如 `findByUsernameAndStatus`。
    3.  根据解析出的关键词（`find`, `By`, `Username`, `And`, `Status`）和参数，构建 JPQL 查询语句。
    4.  在运行时，当调用该方法时，执行预构建的 JPQL。

**3.1.2 常用查询关键词**

| 前缀 | 作用 | 返回值 | 示例 |
|---|---|---|---|
| `find...By` | 查询实体 | `List<T>`, `Optional<T>`, `T` | `findByEmail(String email)` |
| `read...By` | 查询实体 (同 `find`) | 同上 | `readByEmail(String email)` |
| `get...By` | 查询实体 (同 `find`) | 同上 | `getByEmail(String email)` |
| `query...By` | 查询实体 (同 `find`) | 同上 | `queryByEmail(String email)` |
| `count...By` | 统计数量 | `long` | `countByStatus(UserStatus status)` |
| `exists...By`| 判断是否存在 | `boolean` | `existsByEmail(String email)` |
| `delete...By`| 删除实体 | `long` (删除数量), `void` | `deleteByStatus(UserStatus status)` |

**3.1.3 条件关键词详解与示例**

*假设有 `User` 实体，包含 `username`, `email`, `age`, `status`, `createdAt` 等字段。*

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 等值查询
    Optional<User> findByEmail(String email);

    // 2. AND 多条件查询
    Optional<User> findByUsernameAndStatus(String username, UserStatus status);

    // 3. OR 条件查询
    List<User> findByUsernameOrEmail(String username, String email);

    // 4. 模糊查询 (LIKE)
    List<User> findByUsernameContaining(String keyword); // LIKE '%keyword%'
    List<User> findByUsernameStartingWith(String prefix); // LIKE 'prefix%'
    List<User> findByUsernameEndingWith(String suffix);   // LIKE '%suffix'

    // 5. 忽略大小写
    List<User> findByUsernameContainingIgnoreCase(String keyword);

    // 6. 范围查询 (BETWEEN, LessThan, GreaterThan)
    List<User> findByAgeBetween(int startAge, int endAge);
    List<User> findByCreatedAtAfter(LocalDateTime dateTime);
    List<User> findByAgeLessThanEqual(int maxAge);

    // 7. IN 查询
    List<User> findByStatusIn(Collection<UserStatus> statuses);

    // 8. IS NULL / IS NOT NULL
    List<User> findByAgeIsNull();
    List<User> findByAgeIsNotNull();

    // 9. 排序
    List<User> findByStatusOrderByUsernameDesc(UserStatus status);

    // 10. 限制结果集 (Top, First)
    Optional<User> findTopByOrderByAgeDesc(); // 获取年龄最大的用户
    List<User> findFirst5ByStatusOrderByCreatedAtDesc(UserStatus status); // 获取最新创建的5个用户
}
```

**3.1.4 优点与局限性**

*   **优点：**
    *   **极简代码：** 无需编写任何 JPQL/SQL。
    *   **类型安全：** 方法参数和返回值都是强类型的。
    *   **易于理解：** 方法名即查询意图。
*   **局限性：**
    *   **方法名过长：** 复杂查询会导致方法名非常长，难以阅读。
    *   **功能有限：** 不支持 `JOIN`、分组、子查询等复杂操作。
    *   **不适合动态查询：** 如果查询条件是可选的，需要为每种组合编写一个方法。

#### 3.2 `@Query` 注解查询 (JPQL/HQL)

**3.2.1 JPQL 简介**

*   **JPQL (Java Persistence Query Language)：** 面向对象的查询语言，语法类似于 SQL，但操作的是**实体 (Entity)** 和**属性 (Attribute)**，而不是表和列。
*   **与 SQL 的区别：**
    *   **面向对象：** `FROM User u` 而不是 `FROM users`。
    *   **大小写敏感：** 实体名和属性名是大小写敏感的。
    *   **数据库无关：** 由 JPA 实现（如 Hibernate）转换为特定数据库的 SQL 方言。

**3.2.2 使用 `@Query` 进行查询**

*   当方法命名规则无法满足需求时，使用 `@Query` 自定义 JPQL。

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 使用命名参数 (推荐)
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmailAddress(@Param("email") String email);

    // 2. 使用索引参数 (不推荐，可读性差)
    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.status = ?2")
    List<User> findUsersByUsernameAndStatus(String username, UserStatus status);

    // 3. LIKE 查询
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword%")
    List<User> findUsersByUsernameKeyword(@Param("keyword") String keyword);

    // 4. 投影部分字段 (返回 Object[])
    @Query("SELECT u.username, u.email FROM User u WHERE u.id = :id")
    Object[] findUsernameAndEmailById(@Param("id") Long id);
}
```

**3.2.3 执行更新和删除操作**

*   **`@Modifying` 注解：** 告诉 Spring Data JPA 该查询是**更新**或**删除**操作。
*   **`@Transactional` 注解：** 所有修改操作必须在事务中执行。通常在 Service 层添加此注解。

```java
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("UPDATE User u SET u.status = :newStatus WHERE u.lastLoginDate < :date")
    int updateStatusForOldUsers(@Param("newStatus") UserStatus newStatus, @Param("date") LocalDateTime date);

    @Modifying
    @Query("DELETE FROM User u WHERE u.status = :status")
    int deleteInactiveUsers(@Param("status") UserStatus status);
}

// 在 Service 层调用
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void deactivateOldUsers() {
        int updatedCount = userRepository.updateStatusForOldUsers(
            UserStatus.INACTIVE,
            LocalDateTime.now().minusYears(1)
        );
        // ...
    }
}
```
**注意：** 使用 `@Query` 进行更新/删除操作会绕过 JPA 的一级缓存和持久化上下文，可能导致缓存与数据库不一致。通常在执行完此类操作后，应手动 `clear()` EntityManager 或谨慎使用。

#### 3.3 原生 SQL 查询 (`nativeQuery`)

**3.3.1 何时使用原生 SQL**

*   **复杂查询：** 需要使用特定数据库的函数、语法或特性 (如窗口函数、公用表表达式 CTE)。
*   **性能优化：** 手动编写高度优化的 SQL。
*   **操作非实体管理的表：** 查询视图或执行存储过程。

**3.3.2 基本原生 SQL 查询**

*   设置 `@Query` 注解的 `nativeQuery` 属性为 `true`。

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 查询结果映射到实体 (列名需与实体属性匹配或使用别名)
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findUserByEmailNative(@Param("email") String email);

    // 2. 使用索引参数
    @Query(value = "SELECT * FROM users WHERE username = ?1", nativeQuery = true)
    Optional<User> findByUsernameNative(String username);
}
```

**3.3.3 结果映射到 DTO (重点)**

*   当查询结果不是完整的实体时，需要将结果映射到自定义的 DTO (Data Transfer Object)。

**方法一：接口投影 (Interface-based Projection)**

```java
// 1. 定义 DTO 接口
public interface UserSummary {
    String getUsername();
    String getEmail();
    int getAge();
}

// 2. 在 Repository 中使用
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT username, email, age FROM users WHERE status = :status", nativeQuery = true)
    List<UserSummary> findUserSummariesByStatus(@Param("status") String status);
}
```
**要求：** `SELECT` 子句中的列别名必须与接口中的 `get` 方法名匹配 (遵循 JavaBean 规范)。

**方法二：使用 `@SqlResultSetMapping` 和 `@ConstructorResult`**

*   这是最灵活、最强大的方式，但配置也最复杂。

```java
// 1. 定义 DTO 类 (必须有匹配的构造函数)
public class UserDto {
    private String username;
    private int age;
    // 构造函数参数顺序和类型必须与查询列匹配
    public UserDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
    // getters...
}

// 2. 在实体类上定义映射关系
@Entity
@SqlResultSetMapping(
    name = "UserDtoMapping",
    classes = @ConstructorResult(
        targetClass = UserDto.class,
        columns = {
            @ColumnResult(name = "username", type = String.class),
            @ColumnResult(name = "age", type = Integer.class)
        }
    )
)
public class User { ... }

// 3. 在 Repository 中引用映射
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(name = "User.findUserDtos", nativeQuery = true) // 引用 @NamedNativeQuery
    List<UserDto> findUserDtos();
}

// 4. (可选) 在实体上定义命名原生查询
@Entity
@NamedNativeQuery(
    name = "User.findUserDtos",
    query = "SELECT u.username, u.age FROM users u WHERE u.status = 'ACTIVE'",
    resultSetMapping = "UserDtoMapping"
)
public class User { ... }
```

#### 3.4 Specification 动态条件查询

**3.4.1 适用场景**

*   当查询条件是动态组合时，例如在一个复杂的搜索表单中，用户可以选择性地填写多个过滤条件。
*   使用方法命名规则会导致方法爆炸，使用 `@Query` 拼接字符串容易出错且不安全。

**3.4.2 `JpaSpecificationExecutor` 接口**

*   让你的 Repository 接口继承 `JpaSpecificationExecutor<T>`。

```java
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}
```

*   该接口提供了基于 `Specification` 的查询方法：
    *   `Optional<T> findOne(Specification<T> spec);`
    *   `List<T> findAll(Specification<T> spec);`
    *   `Page<T> findAll(Specification<T> spec, Pageable pageable);`
    *   `List<T> findAll(Specification<T> spec, Sort sort);`
    *   `long count(Specification<T> spec);`

**3.4.3 构建 `Specification`**

*   `Specification` 是一个函数式接口，使用 JPA Criteria API 来构建查询条件。

**案例：实现复杂的多条件动态搜索**

```java
// 1. 创建一个 Specification 工具类或直接在 Service 中构建
public class UserSpecifications {

    public static Specification<User> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.isEmpty()) {
                return criteriaBuilder.conjunction(); // 返回一个恒为 true 的条件
            }
            return criteriaBuilder.like(root.get("username"), "%" + username + "%");
        };
    }

    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<User> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (start == null && end == null) {
                return criteriaBuilder.conjunction();
            }
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("createdAt"), start, end);
            }
            if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), end);
        };
    }
}

// 2. 在 Service 中组合 Specification
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Page<User> searchUsers(SearchCriteria criteria, Pageable pageable) {
        Specification<User> spec = Specification
            .where(UserSpecifications.hasUsername(criteria.getUsername()))
            .and(UserSpecifications.hasStatus(criteria.getStatus()))
            .and(UserSpecifications.createdBetween(criteria.getStartDate(), criteria.getEndDate()));

        return userRepository.findAll(spec, pageable);
    }
}

// 3. 定义搜索条件 DTO
public class SearchCriteria {
    private String username;
    private UserStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    // getters and setters
}
```

#### 3.5 Query by Example (QBE)

**3.5.1 核心理念**

*   **按示例查询 (Query by Example)：** 提供一个填充了查询条件的实体对象（“示例”），JPA 根据该对象中非 null 的属性自动构建查询。
*   是另一种实现动态查询的方式，比 Specification 更简单，但功能也更受限。

**3.5.2 `Example` 和 `ExampleMatcher` 的使用**

*   `JpaRepository` 默认继承了 `QueryByExampleExecutor<T>` 接口。

**案例：使用 QBE 实现动态搜索**

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findUsersByExample(User probe) {
        // 1. 创建 ExampleMatcher 来定义匹配规则
        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreCase("username") // 对 username 字段忽略大小写
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 对所有字符串使用模糊查询
            .withIgnoreNullValues() // 忽略 probe 对象中为 null 的属性
            .withIgnorePaths("age", "createdAt"); // 忽略 age 和 createdAt 属性

        // 2. 创建 Example 对象
        Example<User> example = Example.of(probe, matcher);

        // 3. 执行查询
        return userRepository.findAll(example);
    }
}

// 调用
public void search() {
    User probe = new User();
    probe.setUsername("john");
    probe.setStatus(UserStatus.ACTIVE);
    // probe 中的 email, age 等为 null，会被忽略

    List<User> results = userService.findUsersByExample(probe);
}
```

**3.5.3 适用场景与局限性**

*   **适用场景：**
    *   简单的动态查询，主要是等值和模糊查询。
    *   快速实现 UI 表单的后端过滤逻辑。
*   **局限性：**
    *   不支持范围查询 (`>`, `<`, `BETWEEN`)。
    *   不支持 `OR` 逻辑（仅支持所有条件的 `AND` 组合）。
    *   对嵌套/关联属性的支持有限。
    *   灵活性远不如 `Specification`。

---

##### 第四部分：高级查询与特性 (2.5h)

#### 4.1 分页与排序

**4.1.1 `Pageable` 和 `Sort` 对象**

*   **`Sort`：** 封装排序逻辑。
    *   `Sort.by("username").ascending()`
    *   `Sort.by(Sort.Direction.DESC, "createdAt")`
    *   `Sort.by("status").and(Sort.by("username").descending())`
*   **`Pageable`：** 封装分页请求信息（页码、每页大小、排序）。
    *   `PageRequest.of(int page, int size)`
    *   `PageRequest.of(int page, int size, Sort sort)`
    *   **注意：** `page` 是从 0 开始的。

**4.1.2 在 Controller 和 Service 中使用**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public Page<User> getAllUsers(
        // Spring MVC 会自动将请求参数注入到 Pageable 对象
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return userService.findAllUsers(pageable);
    }
}

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
```
*   **请求示例：** `GET /api/users?page=0&size=20&sort=username,asc`

**4.1.3 `Page` 和 `Slice` 的区别**

*   **`Page<T>`：**
    *   继承自 `Slice<T>`。
    *   除了包含当前页的数据和是否有下一页的信息外，还包含**总记录数 (total elements)** 和**总页数 (total pages)**。
    *   为了计算总记录数，JPA 会额外执行一条 `COUNT` 查询。
*   **`Slice<T>`：**
    *   只知道是否有下一页 (`hasNext()`)，不知道总记录数。
    *   通常比 `Page<T>` 性能更好，因为它只执行一条查询。
    *   适用于“无限滚动”加载场景。

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // 返回 Page，会执行 count 查询
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    // 返回 Slice，不会执行 count 查询
    Slice<User> findByAgeGreaterThan(int age, Pageable pageable);
}
```

#### 4.2 多表关联查询

**4.2.1 关联关系映射**

*   **`@ManyToOne` (多对一):** 多个 `Employee` 属于一个 `Department`。
*   **`@OneToMany` (一对多):** 一个 `Department` 有多个 `Employee`。
*   **`@OneToOne` (一对一):** 一个 `User` 对应一个 `UserProfile`。
*   **`@ManyToMany` (多对多):** 一个 `Student` 可以选多门 `Course`，一门 `Course` 可以被多个 `Student` 选。

**示例：`Employee` 和 `Department`**

```java
@Entity
public class Department {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();
    // ...
}

@Entity
public class Employee {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY 是默认值
    @JoinColumn(name = "department_id") // 外键列
    private Department department;
    // ...
}
```

**4.2.2 `fetch` 策略与 N+1 问题**

*   **`FetchType.EAGER` (立即加载):** 查询主实体时，立即通过 `JOIN` 加载关联实体。
    *   **优点：** 简单，不会有 `LazyInitializationException`。
    *   **缺点：** 即使不需要关联数据也加载，可能造成性能浪费。查询列表时，可能导致笛卡尔积问题。
*   **`FetchType.LAZY` (懒加载):** 查询主实体时，不加载关联实体，只在**第一次访问**关联实体时才发送额外的 SQL 查询。
    *   **优点：** 按需加载，性能更高。
    *   **缺点：** 如果在 Session 关闭后访问关联属性，会抛出 `LazyInitializationException`。**是导致 N+1 问题的根源。**

**N+1 问题详解：**

```java
// 1. 查询所有部门 (1条 SQL)
List<Department> departments = departmentRepository.findAll();

// 2. 遍历部门，打印员工姓名
for (Department dept : departments) {
    // 每次调用 dept.getEmployees() 都会触发一条新的 SQL 查询员工
    // 如果有 N 个部门，这里会额外执行 N 条 SQL
    List<Employee> employees = dept.getEmployees();
    System.out.println(employees.get(0).getName());
}
```
**总共执行了 1 + N 条 SQL，这就是 N+1 问题。**

**4.2.3 解决 N+1 问题的方案**

**方案一：`JOIN FETCH` (推荐)**

*   在 JPQL 中使用 `JOIN FETCH` 告诉 JPA 在一次查询中同时加载主实体和关联实体。

```java
@Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```
*   这条查询会生成一条 `LEFT JOIN` SQL，一次性将所有部门和对应的员工查出，解决了 N+1 问题。

**方案二：`@EntityGraph`**

*   以注解的方式声明需要一同加载的关联属性，侵入性比 `JOIN FETCH` 更小。

```java
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @EntityGraph(attributePaths = { "employees" })
    @Override
    List<Department> findAll();
}
```
*   调用 `departmentRepository.findAll()` 时，JPA 会自动生成 `LEFT JOIN` 来加载 `employees`。

**方案三：`@BatchSize`**

*   不是解决 N+1，而是优化它。它将 N 次独立的查询合并为少数几次批量查询。

```java
@Entity
public class Department {
    // ...
    @OneToMany(...)
    @BatchSize(size = 20) // 设置批处理大小
    private List<Employee> employees;
}
```
*   当访问第一个部门的员工时，JPA 会一次性加载 20 个部门的员工数据，后续 19 次访问将直接从缓存获取。将 N+1 变成了 (1 + N/20) 次查询。

#### 4.3 查询结果返回 DTO

**4.3.1 为什么需要 DTO？**

1.  **API 视图定制：** API 只暴露需要的数据，隐藏内部实现细节。
2.  **性能优化：** 只查询数据库中需要的列，减少网络传输和内存占用。
3.  **避免循环引用：** 在双向关联中，直接返回实体可能导致 JSON 序列化死循环。
4.  **解耦：** 表现层与持久层的解耦。

**4.3.2 JPQL 构造函数表达式**

*   在 JPQL 中使用 `NEW` 操作符直接构造 DTO 对象。

```java
// 1. DTO 类，必须有对应的构造函数
public class EmployeeDto {
    private String name;
    private String departmentName;

    public EmployeeDto(String name, String departmentName) {
        this.name = name;
        this.departmentName = departmentName;
    }
    // getters
}

// 2. Repository 方法
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT NEW com.example.jpademo.dto.EmployeeDto(e.name, e.department.name) " +
           "FROM Employee e WHERE e.id = :id")
    Optional<EmployeeDto> findEmployeeDtoById(@Param("id") Long id);
}
```

**4.3.3 接口投影 (Interface-based Projections)**

*   定义一个接口，其中包含需要查询的属性的 `getter` 方法。

```java
// 1. 投影接口
public interface EmployeeSummary {
    String getName();
    String getDepartmentName(); // JPA 会自动处理关联属性 e.department.name
}

// 2. Repository 方法
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<EmployeeSummary> findByStatus(EmployeeStatus status);
}
```
*   **优点：** 无需创建 DTO 类，非常简洁。
*   **动态投影：** 可以通过泛型在运行时决定返回哪种投影。
    ` <T> List<T> findByStatus(EmployeeStatus status, Class<T> type);`

**4.3.4 Class-based Projections (DTOs)**

*   与接口投影类似，但使用 DTO 类。DTO 类必须有构造函数或标准的 setter 方法。
*   如果使用构造函数，其参数名必须与实体属性名匹配。

```java
// 1. DTO 类
public class EmployeeSummaryDto {
    private String name;
    private String departmentName;
    // getters and setters
}

// 2. Repository 方法
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<EmployeeSummaryDto> findByStatus(EmployeeStatus status);
}
```

#### 4.4 复杂统计与分组查询

**4.4.1 JPQL 聚合函数**

*   `COUNT()`: 计数
*   `SUM()`: 求和
*   `AVG()`: 平均值
*   `MAX()`: 最大值
*   `MIN()`: 最小值

**4.4.2 `GROUP BY` 和 `HAVING`**

*   **`GROUP BY`**: 按指定字段对结果进行分组。
*   **`HAVING`**: 对分组后的结果进行过滤。

**案例：按部门统计员工人数和平均薪资**

```java
// 1. 创建结果 DTO
public class DepartmentStats {
    private String departmentName;
    private long employeeCount;
    private double averageSalary;

    public DepartmentStats(String departmentName, long employeeCount, double averageSalary) {
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
    }
    // getters
}

// 2. Repository 方法
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("SELECT NEW com.example.jpademo.dto.DepartmentStats(" +
           "   d.name, " +
           "   COUNT(e.id), " +
           "   AVG(e.salary)" +
           ") " +
           "FROM Department d JOIN d.employees e " +
           "GROUP BY d.name " +
           "HAVING COUNT(e.id) > :minEmployeeCount " +
           "ORDER BY COUNT(e.id) DESC")
    List<DepartmentStats> getDepartmentStats(@Param("minEmployeeCount") long minEmployeeCount);
}
```

#### 4.5 处理 JSON/JSONB 字段 (以 PostgreSQL 为例)

**4.5.1 为什么需要特殊处理**

*   JPA 标准本身不直接支持 JSON 类型。需要借助 JPA 实现（Hibernate）的扩展或第三方库。

**4.5.2 使用 `hypersistence-utils` 库**

*   这是一个强大的第三方库，极大地简化了 Hibernate 对 JSON、数组等高级类型的处理。

**1. 添加依赖**
```xml
<dependency>
    <groupId>io.hypersistence</groupId>
    <artifactId>hypersistence-utils-hibernate-62</artifactId> <!-- 版本号对应 Hibernate 版本 -->
    <version>3.7.0</version>
</dependency>
```

**2. 在实体中使用 `@Type` 注解**
```java
import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "events")
public class Event {
    @Id
    private Long id;

    @Type(JsonType.class) // 告诉 Hibernate 使用 JsonType 来处理这个字段
    @Column(columnDefinition = "jsonb") // 指定数据库列类型为 jsonb
    private Map<String, Object> properties;
    // ...
}
```
*   现在，你可以像操作普通 `Map` 一样读写 `properties` 字段，`hypersistence-utils` 会自动处理 Java Map 和数据库 JSON 之间的转换。

**4.5.3 在原生 SQL 中查询 JSON 字段**

*   对于复杂的 JSON 查询（如查询嵌套 Key），通常需要使用原生 SQL。

```java
public interface EventRepository extends JpaRepository<Event, Long> {

    // 查询 properties 字段中 'source' key 的值为 'mobile' 的事件
    @Query(
        value = "SELECT * FROM events WHERE properties ->> 'source' = :source",
        nativeQuery = true
    )
    List<Event> findEventsBySource(@Param("source") String source);

    // 查询 properties 字段中 'details' -> 'code' 的值大于 100 的事件
    @Query(
        value = "SELECT * FROM events WHERE (properties -> 'details' ->> 'code')::int > :code",
        nativeQuery = true
    )
    List<Event> findEventsByDetailCodeGreaterThan(@Param("code") int code);
}
```
*   **`->`**: 操作符返回 JSON 对象。
*   **`->>`**: 操作符返回文本。
*   **`::int`**: PostgreSQL 的类型转换语法。

##### 第五部分：实战与最佳实践 (1h)

1.  **事务管理 (`@Transactional`)**
    *   声明式事务的用法与传播机制
    *   只读事务 (`readOnly = true`) 的性能优化
2.  **配合 Spring Boot 使用**
    *   `application.properties`/`application.yml` 中的 JPA 相关配置
    *   `spring.jpa.hibernate.ddl-auto` 的正确使用场景
    *   日志与 SQL 格式化 (`show-sql`, `format_sql`)
3.  **API 集成与文档**
    *   添加 `springdoc-openapi` (Swagger) 依赖
    *   为 Repository 接口自动生成 API 文档
    *   启动与访问 API 文档页面

---

##### 第六部分：JPA 常见问题与解决方案 (2h)

#### 6.1 性能相关问题

**1. N+1 查询问题**
*   **问题现象：** 查询一个主实体列表时，每个主实体都会触发额外的查询获取关联数据
*   **解决方案：**
    ```java
    // 问题代码：会产生 N+1 查询
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders;
    
    // 解决方案1：使用 JOIN FETCH
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    User findUserWithOrders(@Param("id") Long id);
    
    // 解决方案2：使用 @EntityGraph
    @EntityGraph(attributePaths = "orders")
    List<User> findAll();
    
    // 解决方案3：使用 @BatchSize
    @OneToMany(fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private List<Order> orders;
    ```

**2. 懒加载异常 (LazyInitializationException)**
*   **问题现象：** 在 Session 关闭后访问懒加载属性
*   **解决方案：**
    ```java
    // 方案1：在事务内完成所有操作
    @Transactional
    public UserDTO getUserWithOrders(Long id) {
        User user = userRepository.findById(id);
        // 在事务内触发懒加载
        List<OrderDTO> orders = user.getOrders().stream()
            .map(this::mapToDTO).collect(Collectors.toList());
        return new UserDTO(user, orders);
    }
    
    // 方案2：使用 JOIN FETCH 预加载
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    User findByIdWithOrders(@Param("id") Long id);
    
    // 方案3：修改为立即加载（谨慎使用）
    @OneToMany(fetch = FetchType.EAGER)
    private List<Order> orders;
    ```

**3. 分页查询性能问题**
*   **问题现象：** 大数据量分页查询很慢，特别是后面的页数
*   **解决方案：**
    ```java
    // 使用游标分页替代偏移量分页
    public interface UserRepository extends JpaRepository<User, Long> {
        @Query("SELECT u FROM User u WHERE u.id > :lastId ORDER BY u.id")
        List<User> findUsersAfterId(@Param("lastId") Long lastId, Pageable pageable);
    }
    
    // 优化 COUNT 查询
    @Query(value = "SELECT u FROM User u WHERE u.status = :status",
           countQuery = "SELECT count(u.id) FROM User u WHERE u.status = :status")
    Page<User> findByStatus(@Param("status") String status, Pageable pageable);
    ```

#### 6.2 数据一致性问题

**4. 乐观锁冲突 (OptimisticLockException)**
*   **问题现象：** 并发更新同一实体时抛出异常
*   **解决方案：**
    ```java
    @Entity
    public class Product {
        @Version
        private Long version;
        
        // 业务代码中处理冲突
        @Service
        @Transactional
        public void updateProduct(Long id, String name) {
            try {
                Product product = productRepository.findById(id).orElseThrow();
                product.setName(name);
                productRepository.save(product);
            } catch (OptimisticLockException e) {
                // 重试或返回错误信息
                throw new BusinessException("数据已被其他用户修改，请刷新后重试");
            }
        }
    }
    ```

**5. 悲观锁死锁问题**
*   **问题现象：** 使用悲观锁时出现死锁
*   **解决方案：**
    ```java
    // 设置锁超时时间
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    Optional<Account> findByIdForUpdate(Long id);
    
    // 统一锁顺序，避免死锁
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Long firstId = Math.min(fromId, toId);
        Long secondId = Math.max(fromId, toId);
        
        Account first = accountRepository.findByIdForUpdate(firstId);
        Account second = accountRepository.findByIdForUpdate(secondId);
        // 执行转账逻辑
    }
    ```

#### 6.3 查询相关问题

**6. JPQL 查询语法错误**
*   **常见错误：**
    ```java
    // 错误：使用了表名而非实体名
    @Query("SELECT * FROM users u WHERE u.name = :name") // ❌
    
    // 正确：使用实体名和具体字段
    @Query("SELECT u FROM User u WHERE u.name = :name") // ✅
    
    // 错误：UPDATE/DELETE 没有 @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id") // ❌
    
    // 正确：添加 @Modifying 和 @Transactional
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id") // ✅
    ```

**7. 原生 SQL 结果映射问题**
*   **问题现象：** 原生 SQL 查询结果无法正确映射到对象
*   **解决方案：**
    ```java
    // 方案1：使用 @SqlResultSetMapping
    @NamedNativeQuery(
        name = "User.findUserStats",
        query = "SELECT u.name, COUNT(o.id) as orderCount FROM users u LEFT JOIN orders o ON u.id = o.user_id GROUP BY u.id, u.name",
        resultSetMapping = "UserStatsMapping"
    )
    @SqlResultSetMapping(
        name = "UserStatsMapping",
        classes = @ConstructorResult(
            targetClass = UserStatsDTO.class,
            columns = {
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "orderCount", type = Long.class)
            }
        )
    )
    
    // 方案2：返回 Object[] 并手动转换
    @Query(value = "SELECT u.name, COUNT(o.id) FROM users u LEFT JOIN orders o ON u.id = o.user_id GROUP BY u.id", nativeQuery = true)
    List<Object[]> findUserOrderCounts();
    ```

**8. 动态查询条件过于复杂**
*   **问题现象：** Specification 代码冗长难维护
*   **解决方案：**
    ```java
    // 创建查询构建器
    public class UserSpecificationBuilder {
        private List<Specification<User>> specifications = new ArrayList<>();
        
        public UserSpecificationBuilder withName(String name) {
            if (StringUtils.hasText(name)) {
                specifications.add((root, query, cb) -> 
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            return this;
        }
        
        public UserSpecificationBuilder withStatus(UserStatus status) {
            if (status != null) {
                specifications.add((root, query, cb) -> 
                    cb.equal(root.get("status"), status));
            }
            return this;
        }
        
        public Specification<User> build() {
            return specifications.stream()
                .reduce(Specification.where(null), Specification::and);
        }
    }
    ```

#### 6.4 配置相关问题

**9. 实体扫描配置问题**
*   **问题现象：** 实体类找不到或重复扫描
*   **解决方案：**
    ```java
    // 明确指定实体包路径
    @EnableJpaRepositories(basePackages = "com.example.repository")
    @EntityScan(basePackages = "com.example.entity")
    @SpringBootApplication
    public class Application {
        // ...
    }
    
    // 或在 application.yml 中配置
    spring:
      jpa:
        packages-to-scan: com.example.entity
    ```

**10. 连接池配置问题**
*   **问题现象：** 连接泄漏或连接不足
*   **解决方案：**
    ```yaml
    spring:
      datasource:
        hikari:
          maximum-pool-size: 20
          minimum-idle: 5
          idle-timeout: 300000
          max-lifetime: 1200000
          connection-timeout: 20000
          leak-detection-threshold: 60000
      jpa:
        properties:
          hibernate:
            connection:
              provider_disables_autocommit: true
    ```

#### 6.5 调试与监控

**11. SQL 调试配置**
*   **完整的调试配置：**
    ```yaml
    spring:
      jpa:
        show-sql: true
        properties:
          hibernate:
            format_sql: true
            use_sql_comments: true
            type: trace
    
    logging:
      level:
        org.hibernate.SQL: DEBUG
        org.hibernate.type.descriptor.sql.BasicBinder: TRACE
        org.springframework.transaction: DEBUG
    ```

**12. 性能监控配置**
*   **启用 Hibernate 统计信息：**
    ```yaml
    spring:
      jpa:
        properties:
          hibernate:
            generate_statistics: true
            session:
              events:
                log:
                  LOG_QUERIES_SLOWER_THAN_MS: 1000
    ```

#### 6.6 实践建议

**最佳实践清单：**
1. **总是使用 @Transactional 进行修改操作**
2. **优先使用 LAZY 加载，必要时使用 JOIN FETCH**
3. **为经常查询的字段创建数据库索引**
4. **使用 DTO 替代直接返回实体**
5. **合理设置连接池大小**
6. **在生产环境关闭 DDL 自动生成**
7. **使用 @Version 实现乐观锁**
8. **定期监控慢查询和连接池状态**