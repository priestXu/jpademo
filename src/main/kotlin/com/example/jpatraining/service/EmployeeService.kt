package com.example.jpatraining.service

import com.example.jpatraining.domain.Company
import com.example.jpatraining.domain.Department
import com.example.jpatraining.domain.Employee
import com.example.jpatraining.dto.DepartmentSalaryStats
import com.example.jpatraining.dto.EmployeeDetailsDto
import com.example.jpatraining.dto.EmployeeDto
import com.example.jpatraining.dto.UpdateEmployeeRequest
import com.example.jpatraining.repository.DepartmentRepository
import com.example.jpatraining.repository.EmployeeRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 员工服务的业务逻辑层。
 * `@Service` 将其声明为 Spring 的服务类。
 * `@Transactional` 默认对所有 public 方法启用事务管理。
 */
@Service
@Transactional
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val departmentRepository: DepartmentRepository,
    private val entityManager: EntityManager, // 注入 EntityManager 用于执行 Criteria 查询
) {

    /**
     * 创建一个新员工。
     */
    fun createEmployee(employee: Employee): Employee = employeeRepository.save(employee)

    /**
     * 根据 ID 获取员工信息。`readOnly = true` 用于优化只读事务。
     */
    @Transactional(readOnly = true)
    fun getEmployeeById(id: Long): Employee? = employeeRepository.findById(id).orElse(null)

    /**
     * 根据 ID 获取员工信息，并预先加载其部门信息以避免 N+1 问题。
     */
    @Transactional(readOnly = true)
    fun getEmployeeByIdWithDepartment(id: Long): Employee? = employeeRepository.findByIdWithDepartment(id)

    /**
     * 分页获取所有员工。
     */
    @Transactional(readOnly = true)
    fun getAllEmployees(pageable: Pageable): Page<Employee> = employeeRepository.findAll(pageable)

    /**
     * 分页获取所有员工，并预先加载部门信息。
     */
    @Transactional(readOnly = true)
    fun getAllEmployeesWithDepartments(pageable: Pageable): Page<Employee> =
        employeeRepository.findAllWithDepartments(pageable)

    /**
     * 更新一个已存在的员工信息。
     * 只更新非空字段，避免覆盖已有数据。
     * 如果部门 ID 存在，则更新员工的部门信息。
     * 如果员工不存在，则返回 null。
     */
    fun updateEmployee(id: Long, request: UpdateEmployeeRequest): Employee? {
        val existingEmployee = employeeRepository.findById(id).orElse(null) ?: return null

        // 只更新非空字段
        request.name?.let { existingEmployee.name = it }
        request.email?.let { existingEmployee.email = it }
        request.salary?.let { existingEmployee.salary = it }

        // 处理部门更新
        request.departmentId?.let { deptId ->
            val department = departmentRepository.findById(deptId).orElse(null)
            existingEmployee.department = department
        }

        return employeeRepository.save(existingEmployee)
    }

    /**
     * 根据 ID 删除员工。
     */
    fun deleteEmployee(id: Long) = employeeRepository.deleteById(id)

    /**
     * 根据邮箱查找员工。
     */
    @Transactional(readOnly = true)
    fun findByEmail(email: String): Employee? = employeeRepository.findByEmail(email)

    /**
     * 根据部门名称查找员工。
     */
    @Transactional(readOnly = true)
    fun findByDepartment(departmentName: String): List<Employee> =
        employeeRepository.findByDepartment_Name(departmentName)

    /**
     * 根据部门名称查找员工（忽略大小写）。
     */
    @Transactional(readOnly = true)
    fun findByDepartmentIgnoreCase(departmentName: String): List<Employee> =
        employeeRepository.findByDepartment_NameIgnoreCase(departmentName)

    /**
     * 使用 Specification 进行动态条件查询，并返回分页结果。
     */
    @Transactional(readOnly = true)
    fun searchEmployees(spec: Specification<Employee>, pageable: Pageable): Page<Employee> =
        employeeRepository.findAll(spec, pageable)

    /**
     * 调用仓库中的动态 @Query 方法。
     *
     * @param name 可选的员工姓名。
     * @param pageable 分页参数。
     * @return 员工分页结果。
     */
    @Transactional(readOnly = true)
    fun findByNameDynamically(name: String?, pageable: Pageable): Page<Employee> {
        return employeeRepository.findByNameDynamically(name, pageable)
    }

    /**
     * 根据 ID 获取员工的 DTO 表示。
     */
    @Transactional(readOnly = true)
    fun getEmployeeDtoById(id: Long): EmployeeDto? = employeeRepository.findEmployeeDtoById(id)

    /**
     * 获取各部门的薪资统计数据。
     */
    @Transactional(readOnly = true)
    fun getDepartmentSalaryStats(): List<DepartmentSalaryStats> = employeeRepository.getDepartmentSalaryStats()

    /**
     * 使用 Criteria API 查询每个部门的员工数量。
     * 这是一个更类型安全、更面向对象的构建查询的方式。
     */
    @Transactional(readOnly = true)
    fun getEmployeeCountByDepartment(): List<Map<String, Any>> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createTupleQuery()
        val root = criteriaQuery.from(Employee::class.java)

        // 定义查询的选择项：部门名 和 员工计数的别名
        criteriaQuery.multiselect(
            root.get<Department>("department").get<String>("name").alias("departmentName"),
            criteriaBuilder.count(root).alias("employeeCount")
        )
        // 定义分组条件
        criteriaQuery.groupBy(root.get<Department>("department").get<String>("name"))

        // 执行查询并转换结果
        val typedQuery = entityManager.createQuery(criteriaQuery)
        return typedQuery.resultList.map { tuple ->
            mapOf(
                "departmentName" to tuple.get("departmentName"),
                "employeeCount" to tuple.get("employeeCount")
            )
        }
    }

    /**
     * 根据可选的部门名称查找员工 DTO。
     */
    @Transactional(readOnly = true)
    fun findEmployeeDtosByDepartmentName(departmentName: String?, pageable: Pageable): Page<EmployeeDto> =
        employeeRepository.findEmployeeDtoByDepartmentName(departmentName, pageable)

    /**
     * 使用 CriteriaBuilder 进行复杂的多表关联查询，获取员工的详细信息，包括部门和公司名称。
     * 这是一个类型安全的查询方式，可以在编译时检查查询的正确性。
     *
     * @param employeeName 可选参数，用于根据员工姓名进行模糊查询。
     * @param companyName 可选参数，用于根据公司名称进行模糊查询。
     * @param pageable 分页和排序参数。
     * @return 返回一个包含员工详细信息的 DTO 的分页结果 (`Page<EmployeeDetailsDto>`)。
     */
    @Transactional(readOnly = true)
    fun findEmployeeDetails(employeeName: String?, companyName: String?, pageable: Pageable): Page<EmployeeDetailsDto> {
        val cb = entityManager.criteriaBuilder

        // --- 创建获取数据的查询 ---
        val cq = cb.createQuery(EmployeeDetailsDto::class.java)
        val root = cq.from(Employee::class.java)
        val departmentJoin = root.join<Employee, Department>("department", JoinType.INNER)
        val companyJoin = departmentJoin.join<Department, Company>("company", JoinType.INNER)

        cq.select(
            cb.construct(
                EmployeeDetailsDto::class.java,
                root.get<Long>("id"),
                root.get<String>("name"),
                root.get<String>("email"),
                departmentJoin.get<String>("name"),
                companyJoin.get<String>("name")
            )
        )

        // 应用查询条件
        val predicates = buildPredicates(cb, root, departmentJoin, companyJoin, employeeName, companyName)
        cq.where(*predicates.toTypedArray())

        // --- 应用排序 ---
        val orders = mutableListOf<Order>()
        pageable.sort.forEach { order ->
            val path = when (order.property) {
                "employeeName" -> root.get<String>("name")
                "departmentName" -> departmentJoin.get<String>("name")
                "companyName" -> companyJoin.get<String>("name")
                else -> root.get(order.property)
            }
            orders.add(if (order.isAscending) cb.asc(path) else cb.desc(path))
        }
        cq.orderBy(orders)

        // --- 执行分页查询 ---
        val query = entityManager.createQuery(cq)
        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize
        val resultList = query.resultList

        // --- 创建并执行 Count 查询 ---
        val countCq = cb.createQuery(Long::class.java)
        val countRoot = countCq.from(Employee::class.java)
        val countDepartmentJoin = countRoot.join<Employee, Department>("department", JoinType.INNER)
        val countCompanyJoin = countDepartmentJoin.join<Department, Company>("company", JoinType.INNER)

        countCq.select(cb.count(countRoot))

        // 使用相同的条件构建方法，但传入count查询的root和join
        val countPredicates =
            buildPredicates(cb, countRoot, countDepartmentJoin, countCompanyJoin, employeeName, companyName)
        countCq.where(*countPredicates.toTypedArray())

        val total = entityManager.createQuery(countCq).singleResult

        return PageImpl(resultList, pageable, total)
    }

    private fun buildPredicates(
        cb: CriteriaBuilder,
        root: Root<Employee>,
        departmentJoin: Join<Employee, Department>,
        companyJoin: Join<Department, Company>,
        employeeName: String?,
        companyName: String?,
    ): List<jakarta.persistence.criteria.Predicate> {
        val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()
        employeeName?.let { predicates.add(cb.like(root.get("name"), "%$it%")) }
        companyName?.let { predicates.add(cb.like(companyJoin.get("name"), "%$it%")) }
        return predicates
    }
}