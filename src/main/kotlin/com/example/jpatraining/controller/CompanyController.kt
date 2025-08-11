package com.example.jpatraining.controller

import com.example.jpatraining.domain.Company
import com.example.jpatraining.service.CompanyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

/**
 * 公司管理的 RESTful API 控制器。
 * `@RestController` 结合了 `@Controller` 和 `@ResponseBody`，使得返回的 Java 对象能自动序列化为 JSON。
 * `@RequestMapping` 定义了该控制器下所有 API 的基础路径。
 * `@Tag` 用于在 Swagger UI 中对 API 进行分组和描述。
 */
@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company API", description = "用于管理公司及其 JSONB 属性的 API")
class CompanyController(private val companyService: CompanyService) {

    /**
     * 创建一个新的公司。
     * `@Operation` 提供了关于此 API 端点的详细描述，会显示在 Swagger UI 中。
     * `@PostMapping` 将 HTTP POST 请求映射到此方法。
     * `@RequestBody` 表示请求体中的 JSON 将被反序列化为 Company 对象。
     * @param company 从请求体中获取的公司数据。
     * @return 创建后的公司对象。
     */
    @Operation(summary = "创建一个新公司")
    @PostMapping
    fun createCompany(@RequestBody company: Company): Company = companyService.createCompany(company)

    /**
     * 根据行业名称（从 JSONB 属性中提取）查找公司。
     * `@GetMapping` 将 HTTP GET 请求映射到此方法。
     * `@RequestParam` 从请求的 URL 查询参数中获取值。
     * @param industry 要搜索的行业名称。
     * @return 匹配的公司列表。
     */
    @Operation(summary = "按行业查找公司 (从 JSONB 属性中查询)")
    @GetMapping("/search/industry/{page}")
    fun findByIndustry(@PathVariable page: Int? = 0, @RequestParam industry: String): Page<Company> =
        companyService.findByIndustry(industry, PageRequest.of(page ?: 0, 10, Sort.by("id").descending()))
}