package com.example.jpatraining.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.persistence.*

/**
 * 部门实体类。
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Department @JvmOverloads constructor(
    /**
     * 部门主键 ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_seq")
    @SequenceGenerator(name = "department_seq", sequenceName = "department_id_seq", allocationSize = 1)
    var id: Long? = null,

    /**
     * 部门名称。
     */
    var name: String,

    /**
     * 部门位置。
     */
    var location: String,

    /**
     * 一对多关系：一个部门可以有多个员工。
     * `mappedBy = "department"` 指明了关系的维护方在 `Employee` 实体的 `department` 字段。
     * 这意味着在 `Department` 端是关系的被维护方，不会有外键列。
     */
    @OneToMany(mappedBy = "department")
    @JsonManagedReference // 用于序列化时避免递归
    val employees: MutableList<Employee> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonBackReference
    var company: Company? = null
) {
    // Manually implement toString to avoid recursion
    override fun toString(): String {
        return "Department(id=$id, name='$name', location='$location')"
    }
}
