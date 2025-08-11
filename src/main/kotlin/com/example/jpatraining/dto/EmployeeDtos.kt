package com.example.jpatraining.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * 员工数据传输对象 (DTO)。
 * 用于在API层传输员工的核心信息，避免暴露整个领域模型。
 * @property id 员工ID
 * @property name 员工姓名
 * @property email 员工邮箱
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EmployeeDto(
    val id: Long?,
    val name: String,
    val email: String,
)

/**
 * 部门薪资统计数据传输对象 (DTO)。
 * 用于封装按部门分组的薪资统计结果。
 * @property department 部门名称
 * @property totalEmployees 部门总员工数
 * @property averageSalary 部门平均薪资
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DepartmentSalaryStats(
    val department: String,
    val totalEmployees: Long,
    val averageSalary: Double,
)

/**
 * 包含员工及其部门名称的数据传输对象。
 * 用于在 API 层返回组合信息，同时避免暴露完整的领域模型。
 * @property id 员工ID
 * @property name 员工姓名
 * @property email 员工邮箱
 * @property departmentName 员工所属部门的名称，可以为 null
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class EmployeeWithDepartmentDto(
    val id: Long?,
    val name: String,
    val email: String,
    val departmentName: String?,
)


/**
 * 更新员工请求体。
 * 用于在更新员工信息时传递可选的字段。
 * @property name 员工姓名，可选
 * @property email 员工邮箱，可选
 * @property departmentId 员工所属部门的 ID，可选
 * @property salary 员工薪资，可选
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UpdateEmployeeRequest(
    val name: String? = null,
    val email: String? = null,
    val departmentId: Long? = null,
    val salary: Double? = null,
)