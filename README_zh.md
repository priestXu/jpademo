# Spring Boot JPA 培训

本项目是一个 Spring Boot JPA 培训课程的演示项目。

## 项目结构

```
.
├── pom.xml
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   └── com
│   │   │       └── example
│   │   │           └── jpatraining
│   │   │               ├── controller
│   │   │               │   ├── CompanyController.kt
│   │   │               │   └── EmployeeController.kt
│   │   │               ├── domain
│   │   │               │   ├── Company.kt
│   │   │               │   ├── Department.kt
│   │   │               │   └── Employee.kt
│   │   │               ├── dto
│   │   │               │   ├── EmployeeDetailsDto.kt
│   │   │               │   ├── EmployeeDtos.kt
│   │   │               │   └── EmployeeWithDepartmentDto.kt
│   │   │               ├── repository
│   │   │               │   ├── CompanyRepository.kt
│   │   │               │   ├── DepartmentRepository.kt
│   │   │               │   ├── EmployeeRepository.kt
│   │   │               │   └── EmployeeSpecifications.kt
│   │   │               ├── service
│   │   │               │   ├── CompanyService.kt
│   │   │               │   └── EmployeeService.kt
│   │   │               ├── DataInitializer.kt
│   │   │               └── JpaTrainingApplication.kt
│   │   └── resources
│   │       └── application.yml
│   └── test
└── training_outline.md
```

## 如何运行

1.  **构建项目:**

    ```bash
    ./mvnw clean install
    ```

2.  **运行应用:**

    ```bash
    ./mvnw spring-boot:run
    ```

## API 文档 (Swagger)

应用运行后，你可以通过以下地址访问 Swagger UI：

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

OpenAPI 规范位于：

[http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## 查询示例 (cURL)

这里有一些如何使用 cURL 查询 API 的示例。

### 1. 获取分页的员工列表
获取第一页的员工数据，每页包含 5 个条目，并按姓名升序排序。
```bash
curl -X GET "http://localhost:8080/api/employees?page=0&size=5&sort=name,asc"
```

### 2. 动态员工搜索
搜索姓名中包含 "a"、在 "Engineering" 部门、且薪水高于 60000 的员工。
```bash
curl -X GET "http://localhost:8080/api/employees/search?name=a&department=Engineering&minSalary=60000"
```

### 3. 获取部门薪资统计
获取每个部门的薪资聚合统计信息（平均值、最大值、最小值）。
```bash
curl -X GET "http://localhost:8080/api/employees/stats/salary-by-department"
```

### 4. 按行业查找公司 (JSONB 查询)
查找 "Technology" 行业的公司。此查询针对数据库中的 `jsonb` 类型字段。
```bash
curl -X GET "http://localhost:8080/api/companies/search/industry/0?industry=Technology"
```

### 5. 复杂连接 - 获取员工详细信息
获取姓名包含 "Doe" 且所在公司名称包含 "Solutions" 的员工的详细信息。这个例子演示了复杂的多表连接查询。
```bash
curl -X GET "http://localhost:8080/api/employees/details?employeeName=Doe&companyName=Solutions&page=0&size=10"
```

### 6. 创建一个新员工
```bash
curl -X POST "http://localhost:8080/api/employees" \
-H "Content-Type: application/json" \
-d '{
  "name": "张三",
  "email": "zhang.san@example.com",
  "salary": 75000,
  "department": {
    "id": 1
  }
}'
```
