package com.example.jpatraining.repository

import com.example.jpatraining.domain.Company
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 公司实体的 Spring Data JPA 仓库接口。
 * 继承自 JpaRepository，提供了基本的 CRUD (创建, 读取, 更新, 删除) 操作。
 */
interface CompanyRepository : JpaRepository<Company, Long> {

    /**
     * 使用原生 SQL 查询来根据 JSONB 类型的 `properties` 字段中的 `industry` 键来查找公司。
     * 这种方法对于查询非结构化数据非常有用。
     * @param industry 要查询的行业名称。
     * @return 匹配指定行业的公司列表。
     */
    @Query(
        value = "SELECT * FROM company WHERE properties ->> 'industry' = :industry",
        nativeQuery = true
    )
    fun findByIndustry(industry: String,pageable: Pageable): Page<Company>
}