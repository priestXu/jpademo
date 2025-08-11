package com.example.jpatraining.domain


import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

/**
 * 员工实体类。
 */
@Entity
@DynamicUpdate  // Hibernate 注解，只更新变化的字段
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Employee(
    /**
     * 员工主键 ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_id_seq", allocationSize = 1)
    var id: Long? = null,

    /**
     * 员工姓名。
     */
    var name: String,

    /**
     * 员工邮箱。
     */
    var email: String,

    /**
     * 多对一关系：多个员工可以属于同一个部门。
     * `@ManyToOne` 定义了多对一关系。
     * `@JoinColumn(name = "department_id")` 指定了外键列的名称。
     * 这是关系的拥有方，会负责维护外键。
     */
    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonBackReference // 用于序列化时避免递归
    var department: Department? = null,

    /**
     * 员工薪资。
     */
    var salary: Double,

    // @Transient // 标记为瞬态字段，不会被持久化到数据库
    // var departmentId: Long? = null // 用于存储部门 ID，避免循环引用
) {
    // Manually implement toString to avoid recursion
    override fun toString(): String {
        return "Employee(id=$id, name='$name', email='$email', salary=$salary, departmentId=${department?.id})"
    }
}