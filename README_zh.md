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

## 查询示例

1.  **方法名查询:**
    *   根据姓名查找员工。
    *   `EmployeeRepository.findByName(name: String): List<Employee>`

2.  **使用 JPQL 的 @Query 查询:**
    *   根据部门名称查找员工。
    *   `EmployeeRepository.findByDepartmentName(departmentName: String): List<Employee>`

3.  **使用原生 SQL 的 @Query 查询:**
    *   使用原生查询根据姓名模糊匹配查找员工。
    *   `EmployeeRepository.findByNameContaining(name: String): List<Employee>`

4.  **Specification 动态查询:**
    *   根据多个条件（如姓名、部门）动态搜索员工。
    *   `EmployeeSpecifications.kt`

5.  **示例查询 (QBE):**
    *   根据示例对象查找员工。
    *   `EmployeeService.findByExample(employee: Employee): List<Employee>`

6.  **分页和排序:**
    *   获取分页和排序的员工列表。
    *   `EmployeeController.getPagedEmployees(...)`

7.  **JPQL 构造函数表达式 (DTO):**
    *   获取只包含姓名和部门名称的员工列表。
    *   `EmployeeRepository.findEmployeeWithDepartmentDtos(): List<EmployeeWithDepartmentDto>`

8.  **基于接口的投影:**
    *   获取员工的简化视图。
    *   `EmployeeRepository.findByName(name: String, type: Class<T>): List<T>`

9.  **分组和聚合:**
    *   按部门统计薪资信息。
    *   `EmployeeRepository.findDepartmentSalaryStats(): List<DepartmentSalaryStats>`

10. **Criteria API DTO 投影:**
    *   使用 Criteria API 获取包含部门信息的员工列表。
    *   `EmployeeService.findEmployeesWithDepartmentUsingCriteria()`

11. **Criteria API 复杂动态查询:**
    *   使用 Criteria API 根据多个可选条件搜索员工。
    *   `EmployeeService.searchEmployees(...)`

12. **使用 CriteriaBuilder 进行复杂多表连接:**
    *   **描述:** 获取包含部门和公司信息的员工列表。此示例演示了如何以类型安全的方式执行复杂连接并将结果映射到自定义 DTO。
    *   **DTO:** `EmployeeDetailsDto.kt`
    *   **服务层方法:** `EmployeeService.findEmployeeDetails(employeeName: String?, companyName: String?): List<EmployeeDetailsDto>`
    *   **控制器端点:** `GET /api/employees/details`
