package com.example.jpatraining.repository

import com.example.jpatraining.domain.Employee
import org.springframework.data.jpa.domain.Specification

/**
 * 提供用于动态查询员工信息的 Spring Data JPA Specification。
 * Specification 是一种以编程方式构建查询条件（WHERE子句）的强大机制。
 */
object EmployeeSpecifications {

    /**
     * 创建一个用于模糊查询员工姓名的 Specification。
     * @param name 要搜索的姓名，如果为 null 则不应用此查询条件。
     * @return 返回一个 Specification，如果 name 不为 null。
     */
    fun hasName(name: String?): Specification<Employee>? {
        return name?.let { 
            Specification { root, _, criteriaBuilder ->
                // 使用 like 实现模糊匹配
                criteriaBuilder.like(root.get("name"), "%$it%")
            }
        }
    }

    /**
     * 创建一个用于精确查询部门名称的 Specification。
     * @param department 要搜索的部门名称，如果为 null 则不应用此查询条件。
     * @return 返回一个 Specification，如果 department 不为 null。
     */
    fun inDepartment(department: String?): Specification<Employee>? {
        return department?.let {
            Specification { root, _, criteriaBuilder ->
                // 使用 equal 实现精确匹配
                criteriaBuilder.equal(root.get<String>("department"), it)
            }
        }
    }

    /**
     * 创建一个用于查询薪资大于等于指定值的 Specification。
     * @param minSalary 最低薪资，如果为 null 则不应用此查询条件。
     * @return 返回一个 Specification，如果 minSalary 不为 null。
     */
    fun hasSalaryGreaterThan(minSalary: Double?): Specification<Employee>? {
        return minSalary?.let {
            Specification { root, _, criteriaBuilder ->
                // 使用 greaterThanOrEqualTo 实现大于等于比较
                criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), it)
            }
        }
    }

    /**
     * 创建一个用于根据部门地理位置进行查询的 Specification。
     * 这是一个多表查询的例子，它会连接 `Employee` 和 `Department` 实体。
     *
     * @param location 要搜索的地理位置，如果为 null 则不应用此查询条件。
     * @return 返回一个 Specification，如果 location 不为 null。
     */
    fun atLocation(location: String?): Specification<Employee>? {
        return location?.let {
            Specification { root, _, criteriaBuilder ->
                // 连接到 department 实体，然后根据 location 字段进行匹配
                val departmentJoin = root.join<Any, Any>("department")
                criteriaBuilder.equal(departmentJoin.get<String>("location"), it)
            }
        }
    }
}