package com.example.jpatraining.controller

import com.example.jpatraining.domain.Employee
import com.example.jpatraining.dto.*
import com.example.jpatraining.repository.EmployeeSpecifications
import com.example.jpatraining.service.EmployeeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 员工管理的 RESTful API 控制器。
 * 提供了对员工资源的全面操作，包括 CRUD、搜索和统计。
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee API", description = "用于管理员工的 API")
class EmployeeController(private val employeeService: EmployeeService) {

    @Operation(summary = "创建一个新员工")
    @PostMapping
    fun createEmployee(@RequestBody employee: Employee): Employee = employeeService.createEmployee(employee)

    @Operation(summary = "根据 ID 获取员工信息")
    @GetMapping("/{id}")
    fun getEmployeeById(@PathVariable id: Long): ResponseEntity<Employee> {
        val employee = employeeService.getEmployeeById(id)
        return if (employee != null) ResponseEntity(employee, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @Operation(summary = "根据 ID 获取员工信息（包含部门详情）")
    @GetMapping("/{id}/with-department")
    fun getEmployeeByIdWithDepartment(@PathVariable id: Long): ResponseEntity<EmployeeWithDepartmentDto> {
        val employee = employeeService.getEmployeeByIdWithDepartment(id)
        // 如果员工存在，转换为 DTO 返回
        return if (employee != null) {
            val dto = EmployeeWithDepartmentDto(
                id = employee.id,
                name = employee.name,
                email = employee.email,
                departmentName = employee.department?.name
            )
            ResponseEntity(dto, HttpStatus.OK)
        } else { // 如果员工不存在，返回 404
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(summary = "分页获取所有员工")
    @GetMapping
    fun getAllEmployees(pageable: Pageable): Page<Employee> = employeeService.getAllEmployees(pageable)

    @Operation(summary = "更新一个已存在的员工")
    @PutMapping("/{id}")
    fun updateEmployee(
        @PathVariable id: Long,
        @RequestBody updatedEmployee: UpdateEmployeeRequest,
    ): ResponseEntity<Employee> {
        val employee = employeeService.updateEmployee(id, updatedEmployee)
        return if (employee != null) ResponseEntity(employee, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @Operation(summary = "根据 ID 删除员工")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 表示成功执行但无返回内容
    fun deleteEmployee(@PathVariable id: Long) = employeeService.deleteEmployee(id)

    @Operation(summary = "根据邮箱查找员工")
    @GetMapping("/search/email")
    fun findByEmail(@RequestParam email: String): ResponseEntity<Employee> {
        val employee = employeeService.findByEmail(email)
        return if (employee != null) ResponseEntity(employee, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @Operation(summary = "根据部门名称查找员工")
    @GetMapping("/search/department")
    fun findByDepartment(@RequestParam departmentName: String): List<Employee> =
        employeeService.findByDepartment(departmentName)

    @Operation(summary = "根据部门名称查找员工（忽略大小写）")
    @GetMapping("/search/department-ignore-case")
    fun findByDepartmentIgnoreCase(@RequestParam departmentName: String): List<Employee> =
        employeeService.findByDepartmentIgnoreCase(departmentName)

    @Operation(summary = "动态搜索员工")
    @GetMapping("/search")
    fun searchEmployees(
        @Parameter(description = "按姓名模糊查询") @RequestParam(required = false) name: String?,
        @Parameter(description = "按部门精确查询") @RequestParam(required = false) department: String?,
        @Parameter(description = "按最低薪资查询") @RequestParam(required = false) minSalary: Double?,
        @Parameter(description = "按地理位置查询") @RequestParam(required = false) location: String?,
        @Parameter(hidden = true) pageable: Pageable,
    ): Page<Employee> {
        val spec = Specification.where(EmployeeSpecifications.hasName(name))
            .and(EmployeeSpecifications.inDepartment(department))
            .and(EmployeeSpecifications.hasSalaryGreaterThan(minSalary))
            .and(EmployeeSpecifications.atLocation(location))
        return employeeService.searchEmployees(spec, pageable)
    }

    @Operation(summary = "使用 @Query 和 CASE WHEN 动态查询员工")
    @GetMapping("/search/dynamic-query")
    fun searchEmployeesDynamicQuery(
        @Parameter(description = "按姓名模糊查询") @RequestParam(required = false) name: String?,
        @Parameter(hidden = true) pageable: Pageable,
    ): Page<Employee> {
        return employeeService.findByNameDynamically(name, pageable)
    }

    @Operation(summary = "分页查询员工并返回 DTO")
    @GetMapping("/paged-dto")
    fun getPagedEmployeeDtos(
        @Parameter(hidden = true) pageable: Pageable,
    ): Page<EmployeeWithDepartmentDto> {
        return employeeService.getAllEmployeesWithDepartments(pageable).map {
            EmployeeWithDepartmentDto(it.id, it.name, it.email, it.department?.name)
        }
    }

    @Operation(summary = "获取各部门的薪资统计")
    @GetMapping("/stats/salary-by-department")
    fun getDepartmentSalaryStats(): List<DepartmentSalaryStats> = employeeService.getDepartmentSalaryStats()

    @Operation(summary = "获取各部门的员工数量")
    @GetMapping("/stats/employee-count-by-department")
    fun getEmployeeCountByDepartment(): List<Map<String, Any>> = employeeService.getEmployeeCountByDepartment()

    @Operation(summary = "根据 ID 获取员工的 DTO")
    @GetMapping("/{id}/dto")
    fun getEmployeeDtoById(@PathVariable id: Long): ResponseEntity<EmployeeDto> {
        val employeeDto = employeeService.getEmployeeDtoById(id)
        return if (employeeDto != null) ResponseEntity(
            employeeDto,
            HttpStatus.OK
        ) else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @Operation(summary = "根据可选的部门名称查找员工 DTO")
    @GetMapping("/dtos/by-department/{page}")
    fun findEmployeeDtosByDepartmentName(
        @PathVariable page: Int? = 0,
        @RequestParam(required = false) departmentName: String?,
    ): Page<EmployeeDto> =
        employeeService.findEmployeeDtosByDepartmentName(departmentName, PageRequest.of(page?:0, 10))

    @Operation(summary = "多表关联动态查询员工详细信息")
    @GetMapping("/details")
    fun getEmployeeDetails(
        @RequestParam(required = false) employeeName: String?,
        @RequestParam(required = false) companyName: String?,
        pageable: Pageable
    ): Page<EmployeeDetailsDto> {
        return employeeService.findEmployeeDetails(employeeName, companyName, pageable)
    }
}