package com.example.jpatraining.repository

import com.example.jpatraining.domain.Employee
import com.example.jpatraining.dto.DepartmentSalaryStats
import com.example.jpatraining.dto.EmployeeDetailsDto
import com.example.jpatraining.dto.EmployeeDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 * 员工实体的 Spring Data JPA 仓库接口。
 * - `JpaRepository` 提供了标准的 CRUD 功能。
 * - `JpaSpecificationExecutor` 允许使用 Specification 进行动态条件查询。
 */
interface EmployeeRepository : JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    /**
     * 使用 JPQL 和 CASE WHEN 语句实现动态查询。
     * 当 `name` 参数不为 null 或空字符串时，会根据姓名进行模糊匹配；否则，该条件被忽略。
     *
     * @param name 可选的员工姓名，用于模糊匹配。
     * @param pageable 分页和排序参数。
     * @return 返回员工信息的分页结果。
     */
    @Query(
        """
        SELECT e 
        FROM Employee e 
        WHERE (CASE WHEN :name IS NOT NULL AND :name <> '' THEN lower(e.name) LIKE lower(concat('%', :name, '%')) ELSE TRUE END) = TRUE
    """
    )
    fun findByNameDynamically(name: String?, pageable: Pageable): Page<Employee>

    /**
     * 根据邮箱地址查找员工。Spring Data JPA 会根据方法名自动生成查询。
     * @param email 要查找的邮箱。
     * @return 找到的员工对象，如果不存在则返回 null。
     */
    fun findByEmail(email: String): Employee?

    /**
     * 根据部门名称查找员工。`_` 用来表示属性的级联访问。
     * @param departmentName 要查找的部门名称。
     * @return 属于该部门的员工列表。
     */
    fun findByDepartment_Name(departmentName: String): List<Employee>

    /**
     * 根据部门名称查找员工，忽略大小写。
     *
     * @param departmentName 要查找的部门名称。
     * @return 属于该部门的员工列表。
     */
    @Query("SELECT e FROM Employee e WHERE lower(e.department.name) = lower(:departmentName)")
    fun findByDepartment_NameIgnoreCase(departmentName: String): List<Employee>

    /**
     * 使用 JPQL 查询，根据 ID 查找员工并将其投影为 `EmployeeDto`。
     * 这种方式可以只查询需要的字段，提高效率。
     * @param id 员工 ID。
     * @return 对应的 `EmployeeDto`，如果不存在则返回 null。
     */
    @Query("SELECT new com.example.jpatraining.dto.EmployeeDto(e.id, e.name, e.email) FROM Employee e WHERE e.id = :id")
    fun findEmployeeDtoById(id: Long): EmployeeDto?

    /**
     * 使用 JPQL 查询，计算每个部门的薪资统计信息（员工总数、平均薪资）。
     * @return 包含各部门薪资统计信息的列表。
     */
    @Query(
        """
        SELECT new com.example.jpatraining.dto.DepartmentSalaryStats(e.department.name, COUNT(e), AVG(e.salary))
        FROM Employee e
        GROUP BY e.department.name
    """
    )
    fun getDepartmentSalaryStats(): List<DepartmentSalaryStats>

    /**
     * 使用 `LEFT JOIN FETCH` 一次性查询员工及其关联的部门信息。
     * 这可以解决 N+1 查询问题，在访问 `employee.department` 时不会触发额外的 SQL 查询。
     * @param id 员工 ID。
     * @return 找到的员工对象（包含部门信息），如果不存在则返回 null。
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.id = :id")
    fun findByIdWithDepartment(id: Long): Employee?

    /**
     * 使用 `LEFT JOIN FETCH` 查询所有员工，并预先加载其部门信息，支持分页。使用FETCH解决N+1的问题
     * @param pageable 分页参数。
     * @return 包含员工信息（及部门信息）的分页结果。
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department")
    fun findAllWithDepartments(pageable: Pageable): Page<Employee>

    /**
     * 根据可选的部门名称查询员工 DTO。
     * 如果 `departmentName` 为 null，则查询所有员工。
     * @param departmentName 可选的部门名称。
     * @return 匹配条件的员工 DTO 列表。
     */
    @Query("SELECT new com.example.jpatraining.dto.EmployeeDto(e.id, e.name, e.email) FROM Employee e WHERE (:departmentName IS NULL OR e.department.name = :departmentName)")
    fun findEmployeeDtoByDepartmentName(departmentName: String?, pageable: Pageable): Page<EmployeeDto>


    @Query(
        """
        SELECT new com.example.jpatraining.dto.EmployeeDetailsDto(
            e.id, e.name, e.email, d.name, c.name
        )
        FROM Employee e
        JOIN e.department d
        JOIN d.company c
        WHERE (:employeeName IS NULL OR e.name LIKE %:employeeName%)
        AND (:companyName IS NULL OR c.name LIKE %:companyName%)
    """
    )
    fun findEmployeeDetails(
        @Param("employeeName") employeeName: String?,
        @Param("companyName") companyName: String?,
        pageable: Pageable,
    ): Page<EmployeeDetailsDto>
}