# Spring Boot JPA Training

This project is a demonstration for a Spring Boot JPA training session.

## Project Structure

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

## How to Run

1.  **Build the project:**

    ```bash
    ./mvnw clean install
    ```

2.  **Run the application:**

    ```bash
    ./mvnw spring-boot:run
    ```

## API Documentation (Swagger)

Once the application is running, you can access the Swagger UI at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

The OpenAPI specification is available at:

[http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Query Examples

1.  **Method Name Query:**
    *   Find employees by name.
    *   `EmployeeRepository.findByName(name: String): List<Employee>`

2.  **@Query with JPQL:**
    *   Find employees by department name.
    *   `EmployeeRepository.findByDepartmentName(departmentName: String): List<Employee>`

3.  **@Query with Native SQL:**
    *   Find employees by a partial match on the name using a native query.
    *   `EmployeeRepository.findByNameContaining(name: String): List<Employee>`

4.  **Specification for Dynamic Queries:**
    *   Dynamically search for employees based on multiple criteria (e.g., name, department).
    *   `EmployeeSpecifications.kt`

5.  **Query by Example (QBE):**
    *   Find employees by an example object.
    *   `EmployeeService.findByExample(employee: Employee): List<Employee>`

6.  **Paging and Sorting:**
    *   Get a paginated and sorted list of employees.
    *   `EmployeeController.getPagedEmployees(...)`

7.  **JPQL Constructor Expression (DTO):**
    *   Get a list of employees with only their name and department name.
    *   `EmployeeRepository.findEmployeeWithDepartmentDtos(): List<EmployeeWithDepartmentDto>`

8.  **Interface-based Projection:**
    *   Get a simplified view of employees.
    *   `EmployeeRepository.findByName(name: String, type: Class<T>): List<T>`

9.  **Group By and Aggregation:**
    *   Get salary statistics by department.
    *   `EmployeeRepository.findDepartmentSalaryStats(): List<DepartmentSalaryStats>`

10. **Criteria API for DTO Projection:**
    *   Get a list of employees with department information using Criteria API.
    *   `EmployeeService.findEmployeesWithDepartmentUsingCriteria()`

11. **Criteria API for Complex Dynamic Queries:**
    *   Search for employees with multiple optional criteria using Criteria API.
    *   `EmployeeService.searchEmployees(...)`

12. **CriteriaBuilder for Complex Multi-table Joins:**
    *   **Description:** Fetches a list of employees with their department and company information. This example demonstrates a type-safe way to perform complex joins and map the result to a custom DTO.
    *   **DTO:** `EmployeeDetailsDto.kt`
    *   **Service Method:** `EmployeeService.findEmployeeDetails(employeeName: String?, companyName: String?): List<EmployeeDetailsDto>`
    *   **Controller Endpoint:** `GET /api/employees/details`