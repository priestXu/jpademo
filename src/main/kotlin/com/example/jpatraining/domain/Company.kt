package com.example.jpatraining.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type

/**
 * 公司实体类。
 * `@Entity` 标记这是一个 JPA 实体。
 * `data class` 自动生成 `equals()`, `hashCode()`, `toString()` 等方法。
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Company(
    /**
     * 主键 ID。
     * `@Id` 声明为主键。
     * `@GeneratedValue` 指定主键生成策略为序列（Sequence）。
     * `@SequenceGenerator` 定义了序列的名称和属性。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @SequenceGenerator(name = "company_seq", sequenceName = "company_id_seq", allocationSize = 1)
    var id: Long? = null,

    /**
     * 公司名称。
     */
    var name: String,

    /**
     * 公司的附加属性，以 JSONB 格式存储在数据库中。
     * `@Type(JsonType::class)` 使用了 Hypersistence Utils 库来将 Map 映射到 JSONB 数据库列。
     * `@Column(columnDefinition = "jsonb")` 明确指定了数据库列的类型为 `jsonb`。
     */
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var properties: Map<String, Any> = mutableMapOf(),

    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference
    var departments: MutableList<Department> = mutableListOf()
){
    /**
     * 手动实现 toString 方法以避免递归调用。
     * 只输出必要的字段，避免循环引用。
     */
    override fun toString(): String {
        return "Company(id=$id, name='$name', properties=$properties)"
    }
}
