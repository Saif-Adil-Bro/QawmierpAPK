package com.qawmierp.apk.core.network.dto

import kotlinx.serialization.Serializable

// --- Exams, Admissions, Leaves & Certificates ---

@Serializable
data class ExamDto(
    val id: String = "",
    val name: String = "",
    val exam_type: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val status: String? = null
)

@Serializable
data class CreateExamRequest(
    val name: String,
    val exam_type: String? = null,
    val start_date: String? = null,
    val end_date: String? = null
)

@Serializable
data class ExamResultDto(
    val id: String = "",
    val exam_id: String? = null,
    val student_id: String? = null,
    val subject_id: String? = null,
    val marks_obtained: Double = 0.0,
    val total_marks: Double = 100.0,
    val grade: String? = null,
    val student_name: String? = null,
    val subject_name: String? = null
)

@Serializable
data class ExamRoutineDto(
    val id: String = "",
    val exam_id: String? = null,
    val class_name: String? = null,
    val subject_name: String? = null,
    val exam_date: String? = null,
    val start_time: String? = null,
    val end_time: String? = null
)

@Serializable
data class QuestionDto(
    val id: String = "",
    val subject_name: String? = null,
    val question_text: String = "",
    val marks: Int? = null
)

@Serializable
data class AdmissionDto(
    val id: String = "",
    val applicant_name: String = "",
    val guardian_name: String? = null,
    val phone: String? = null,
    val applied_class: String? = null,
    val status: String = "pending"
)

@Serializable
data class AlumniDto(
    val id: String = "",
    val full_name: String = "",
    val graduation_year: String? = null,
    val phone: String? = null
)

@Serializable
data class LeaveDto(
    val id: String = "",
    val applicant_name: String? = null,
    val reason: String? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val status: String = "pending"
)

@Serializable
data class CreateLeaveRequest(
    val reason: String,
    val start_date: String,
    val end_date: String
)

@Serializable
data class HolidayDto(
    val id: String = "",
    val title: String = "",
    val start_date: String? = null,
    val end_date: String? = null
)

// --- Finance, Accounting, Zakat & Fundraising ---

@Serializable
data class ExpenseDto(
    val id: String = "",
    val title: String = "",
    val category: String? = null,
    val amount: Double = 0.0,
    val expense_date: String? = null,
    val remarks: String? = null
)

@Serializable
data class CreateExpenseRequest(
    val title: String,
    val category: String? = null,
    val amount: Double,
    val expense_date: String,
    val remarks: String? = null
)

@Serializable
data class FundDto(
    val id: String = "",
    val name: String = "",
    val fund_type: String? = null,
    val balance: Double = 0.0,
    val description: String? = null
)

@Serializable
data class DonorDto(
    val id: String = "",
    val full_name: String = "",
    val phone: String? = null,
    val address: String? = null,
    val total_donated: Double? = null
)

@Serializable
data class DonationDto(
    val id: String = "",
    val donor_id: String? = null,
    val donor_name: String? = null,
    val fund_id: String? = null,
    val amount: Double = 0.0,
    val donation_date: String? = null,
    val payment_method: String? = null
)

@Serializable
data class CreateDonationRequest(
    val donor_name: String,
    val amount: Double,
    val donation_date: String,
    val fund_id: String? = null,
    val payment_method: String = "cash"
)

@Serializable
data class FundraisingDto(
    val id: String = "",
    val title: String = "",
    val event_date: String? = null,
    val target_amount: Double = 0.0,
    val collected_amount: Double = 0.0,
    val location: String? = null
)
