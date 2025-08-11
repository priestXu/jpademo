package com.example.jpatraining.dto

data class EmployeeDetailsDto(
    val employeeId: Long?,
    val employeeName: String,
    val employeeEmail: String,
    val departmentName: String,
    val companyName: String
)
