package com.example.jpatraining

import com.example.jpatraining.domain.Company
import com.example.jpatraining.domain.Department
import com.example.jpatraining.domain.Employee
import com.example.jpatraining.repository.CompanyRepository
import com.example.jpatraining.repository.DepartmentRepository
import com.example.jpatraining.repository.EmployeeRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * 数据初始化器，在 Spring Boot 应用启动后执行。
 * 实现 `CommandLineRunner` 接口，`run` 方法会在应用上下文加载完毕后被调用。
 * `@Component` 注解使其成为一个 Spring 管理的 Bean，可以注入其他依赖。
 * 主要用于在开发和测试阶段向数据库填充初始数据。
 */
@Component
class DataInitializer(
    private val employeeRepository: EmployeeRepository,
    private val companyRepository: CompanyRepository,
    private val departmentRepository: DepartmentRepository
) : CommandLineRunner {

    /**
     * 在应用启动时执行此方法，用于插入初始数据。
     * @param args 命令行传入的参数（此处未使用）。
     */
    override fun run(vararg args: String?) {
        // 创建公司
        val techCorp = Company(name = "TechCorp", properties = mapOf("industry" to "Technology", "employees" to 500))
        val healthInc = Company(name = "HealthInc", properties = mapOf("industry" to "Healthcare", "employees" to 1200))
        val shopify = Company(name = "Shopify", properties = mapOf("industry" to "Technology", "employees" to 10000))

        // 创建部门并关联到公司
        val hrDept = Department(name = "HR", location = "Building A", company = techCorp)
        val itDept = Department(name = "IT", location = "Building B", company = techCorp)
        val salesDept = Department(name = "Sales", location = "Building C", company = healthInc)

        // 将部门添加到公司的部门列表中
        techCorp.departments.addAll(listOf(hrDept, itDept))
        healthInc.departments.add(salesDept)

        // 保存公司，由于级联设置，部门也会被保存
        companyRepository.saveAll(listOf(techCorp, healthInc, shopify))

        // 创建并保存几个员工，并关联到相应的部门
        employeeRepository.saveAll(
            listOf(
                Employee(name = "Alice", email = "alice@example.com", department = hrDept, salary = 60000.0),
                Employee(name = "Bob", email = "bob@example.com", department = itDept, salary = 80000.0),
                Employee(name = "Charlie", email = "charlie@example.com", department = itDept, salary = 90000.0),
                Employee(name = "David", email = "david@example.com", department = salesDept, salary = 75000.0)
            )
        )
    }
}