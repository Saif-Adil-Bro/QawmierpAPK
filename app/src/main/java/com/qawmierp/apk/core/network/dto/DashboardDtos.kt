package com.qawmierp.apk.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardDto(
    val students_count: Int = 0,
    val teachers_count: Int = 0,
    val classes_count: Int = 0,
    val today_present: Int = 0,
    val today_absent: Int = 0,
    val today_income: Double = 0.0,
    val today_expense: Double = 0.0,
    val total_income: Double = 0.0,
    val total_expense: Double = 0.0,
    val net_balance: Double = 0.0,
    val monthly_income_expense: List<MonthlyIncomeExpenseDto> = emptyList(),
    val today_attendance: List<AttendanceChartSliceDto> = emptyList(),
    val thirty_day_attendance: List<DailyAttendancePointDto> = emptyList(),
    val exam_pass_rate: List<ExamPassRateDto> = emptyList()
)

@Serializable
data class MonthlyIncomeExpenseDto(
    val month: String = "",
    val income: Double = 0.0,
    val expense: Double = 0.0
)

@Serializable
data class AttendanceChartSliceDto(
    val status: String = "",
    val count: Int = 0
)

@Serializable
data class DailyAttendancePointDto(
    val date: String = "",
    val present: Int = 0,
    val absent: Int = 0
)

@Serializable
data class ExamPassRateDto(
    val exam_name: String = "",
    val pass_rate: Double = 0.0
)

@Serializable
data class UniversalSearchDto(
    val students: List<StudentDto> = emptyList(),
    val teachers: List<TeacherDto> = emptyList(),
    val classes: List<ClassDto> = emptyList()
)
