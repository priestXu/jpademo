package com.example.jpatraining.service

import com.example.jpatraining.domain.Company
import com.example.jpatraining.repository.CompanyRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 公司服务的业务逻辑层。
 * `@Service` 注解将其标记为 Spring 的服务组件。
 * `@Transactional` 注解确保该类中所有公共方法都在事务中执行。
 */
@Service
@Transactional
class CompanyService(private val companyRepository: CompanyRepository) {

    /**
     * 创建一个新的公司。
     * @param company 要创建的公司对象。
     * @return 已保存的公司对象。
     */
    fun createCompany(company: Company): Company = companyRepository.save(company)

    /**
     * 根据行业名称查找公司。
     * `@Transactional(readOnly = true)` 优化了只读查询的性能。
     * @param industry 行业名称。
     * @return 属于该行业的公司列表。
     */
    @Transactional(readOnly = true)
    fun findByIndustry(industry: String,pageable: Pageable): Page<Company> = companyRepository.findByIndustry(industry,pageable)
}