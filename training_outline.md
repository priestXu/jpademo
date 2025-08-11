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

1.  **方法命名规则查询 (Query Methods)**
    *   根据方法名自动生成查询
    *   `findBy...`, `countBy...`, `existsBy...`, `deleteBy...`
    *   组合条件、排序、限制结果集 (`Top`, `First`)
2.  **`@Query` 注解查询 (JPQL/HQL)**
    *   使用 JPQL (Java Persistence Query Language) 进行查询
    *   命名参数 (`:name`) 与索引参数 (`?1`)
    *   执行更新和删除操作 (`@Modifying` 与 `@Transactional`)
3.  **原生 SQL 查询 (`nativeQuery`)**
    *   何时使用原生 SQL
    *   `@Query(value = "...", nativeQuery = true)`
    *   原生 SQL 查询的参数绑定
    *   **重点：** 原生 SQL 查询结果映射到非托管实体或 DTO
4.  **Specification 动态条件查询**
    *   `JpaSpecificationExecutor` 接口介绍
    *   使用 `Specification` 构建动态、可复用的查询条件
    *   **案例：** 实现一个复杂的多条件动态搜索功能（例如：根据用户名、邮箱、创建时间范围等动态查询）
5.  **Query by Example (QBE)**
    *   `Example` 和 `ExampleMatcher` 的使用
    *   声明式动态查询的又一种选择
    *   适用场景与局限性

##### 第四部分：高级查询与特性 (2.5h)

1.  **分页与排序**
    *   `Pageable` 和 `Sort` 对象的使用
    *   `Page` 和 `Slice` 对象的区别与应用
    *   结合不同查询方式实现分页
2.  **多表关联查询**
    *   **关联关系映射：** `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`
    *   `fetch` 策略：`EAGER` vs. `LAZY` (N+1 问题详解与优化)
    *   在 JPQL 中使用 `JOIN` 和 `LEFT JOIN FETCH`
3.  **查询结果返回 DTO (Data Transfer Object)**
    *   **为什么需要 DTO？** 避免暴露实体、按需选择字段
    *   **JPQL/HQL 构造函数表达式：** `SELECT NEW com.example.UserDTO(...) FROM User u`
    *   **接口投影 (Interface-based Projections)**
    *   **Class-based Projections (DTOs)**
4.  **复杂统计与分组查询**
    *   `GROUP BY`, `HAVING`
    *   聚合函数 `COUNT`, `SUM`, `AVG`, `MAX`, `MIN`
    *   **案例：** 按部门统计员工人数和平均薪资
5.  **处理 JSON/JSONB 字段 (以 PostgreSQL 为例)**
    *   使用 `@Type` 注解配合 `hypersistence-utils` 或自定义 `UserType`
    *   在原生 SQL 中使用数据库特定的 JSON 函数进行查询
    *   **案例：** 查询 JSON 字段中特定 key 的 value

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