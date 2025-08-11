package com.example.jpatraining.repository

import com.example.jpatraining.domain.Department
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 部门实体的 Spring Data JPA 仓库接口。
 * 继承自 JpaRepository，自动获得了对 Department 实体的标准 CRUD 操作能力。
 * 无需额外代码即可实现如 `save()`, `findById()`, `findAll()`, `deleteById()` 等方法。
 */
interface DepartmentRepository : JpaRepository<Department, Long>