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

## Query Examples (cURL)

Here are some examples of how to query the API using cURL.

### 1. Get Paged List of Employees
Fetches the first page of employees, with 5 items per page, sorted by name in ascending order.
```bash
curl -X GET "http://localhost:8080/api/employees?page=0&size=5&sort=name,asc"
```

### 2. Dynamic Employee Search
Searches for employees with "a" in their name, in the "Engineering" department, and with a salary greater than 60000.
```bash
curl -X GET "http://localhost:8080/api/employees/search?name=a&department=Engineering&minSalary=60000"
```

### 3. Get Department Salary Statistics
Fetches aggregated salary statistics (average, max, min) for each department.
```bash
curl -X GET "http://localhost:8080/api/employees/stats/salary-by-department"
```

### 4. Find Companies by Industry (JSONB Query)
Finds companies in the "Technology" industry. This query targets a `jsonb` field in the database.
```bash
curl -X GET "http://localhost:8080/api/companies/search/industry/0?industry=Technology"
```

### 5. Complex Join - Get Employee Details
Fetches detailed information for employees whose name contains "Doe" and work for a company whose name contains "Solutions". This demonstrates a complex, multi-table join.
```bash
curl -X GET "http://localhost:8080/api/employees/details?employeeName=Doe&companyName=Solutions&page=0&size=10"
```

### 6. Create a New Employee
```bash
curl -X POST "http://localhost:8080/api/employees" 
-H "Content-Type: application/json" 
-d 
'{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "salary": 75000,
  "department": {
    "id": 1
  }
}'
```
