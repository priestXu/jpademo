package com.example.jpatraining

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Spring Boot 应用主类。
 * `@SpringBootApplication` 注解开启了 Spring Boot 的自动配置、组件扫描等核心功能。
 */
@SpringBootApplication
class JpaTrainingApplication

/**
 * 应用主入口函数。
 * `runApplication<JpaTrainingApplication>(*args)` 会启动整个 Spring Boot 应用。
 * @param args 命令行参数。
 */
fun main(args: Array<String>) {
    runApplication<JpaTrainingApplication>(*args)
}