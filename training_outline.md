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

1.  **JPA 是什么？**
    *   JPA (Java Persistence API) 规范介绍
    *   ORM (Object-Relational Mapping) 思想解析
    *   JPA 与 Hibernate, EclipseLink 等实现的关系
2.  **为什么选择 JPA？—— 与主流 ORM 框架对比**
    *   **JPA vs. MyBatis:**
        *   开发效率与 SQL 控制权的权衡
        *   对象关系映射的自动化程度
        *   类型安全与重构支持
    *   **JPA vs. Hibernate:**
        *   规范与实现的关系
        *   API 的标准化与可移植性
3.  **Spring Data JPA 介绍**
    *   Spring Data JPA 的核心理念：简化数据访问层开发
    *   Repository 抽象的核心优势

##### 第二部分：Spring Boot JPA 基础实践 (2h)

1.  **环境准备与项目初始化**
    *   使用 Spring Initializr 创建一个基于 Maven/Gradle 的 Spring Boot 项目 (Java 17 / Kotlin)
    *   引入 `spring-boot-starter-data-jpa` 和数据库驱动 (H2/PostgreSQL)
2.  **核心注解与实体映射**
    *   `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
    *   `@Column`, `@Basic`, `@Transient`
    *   字段类型映射与最佳实践
3.  **第一个 Repository：`JpaRepository` 详解**
    *   `CrudRepository`, `PagingAndSortingRepository`, `JpaRepository` 的层级关系
    *   开箱即用的 CRUD (Create, Read, Update, Delete) 方法实践

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
