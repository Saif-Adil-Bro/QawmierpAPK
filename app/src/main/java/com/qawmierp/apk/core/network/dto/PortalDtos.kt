package com.qawmierp.apk.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentPortalDto(
    val students: List<StudentDto> = emptyList(),
    val active_student: StudentDto? = null,
    val attendance: List<AttendanceDto> = emptyList(),
    val fees: List<StudentFeeDto> = emptyList(),
    val exam_results: List<ExamResultSummaryDto> = emptyList(),
    val hifz_logs: List<HifzLogDto> = emptyList(),
    val kitab_logs: List<KitabLogDto> = emptyList(),
    val notices: List<NoticeDto> = emptyList()
)

@Serializable
data class TeacherPortalDto(
    val teacher: TeacherDto? = null,
    val assigned_subjects: List<SubjectSummaryDto> = emptyList(),
    val routines: List<RoutineDto> = emptyList(),
    val classes: List<ClassDto> = emptyList(),
    val notices: List<NoticeDto> = emptyList()
)

@Serializable
data class ExamResultSummaryDto(
    val id: String = "",
    val exam_name: String? = null,
    val subject_name: String? = null,
    val marks_obtained: Double? = null,
    val total_marks: Double? = null,
    val grade: String? = null
)

@Serializable
data class SubjectSummaryDto(
    val id: String = "",
    val name: String = "",
    val class_name: String? = null
)

@Serializable
data class RoutineDto(
    val id: String = "",
    val day_of_week: String? = null,
    val start_time: String? = null,
    val end_time: String? = null,
    val subject_name: String? = null,
    val class_name: String? = null
)

@Serializable
data class NoticeDto(
    val id: String = "",
    val title: String = "",
    val content: String? = null,
    val created_at: String? = null
)
