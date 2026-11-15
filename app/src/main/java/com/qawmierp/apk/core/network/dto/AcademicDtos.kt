package com.qawmierp.apk.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClassDto(
    val id: String,
    val name: String = "",
    val department: String? = null
)

@Serializable
data class StudentDto(
    val id: String,
    val madrasa_id: String? = null,
    val full_name: String = "",
    val roll_number: String? = null,
    val student_id_code: String? = null,
    val class_id: String? = null,
    val guardian_name: String? = null,
    val parent_phone: String? = null,
    val photo_url: String? = null,
    val status: String? = null,
    val gender: String? = null,
    val classes: ClassDto? = null
)

@Serializable
data class TeacherDto(
    val id: String,
    val full_name: String = "",
    val phone: String? = null,
    val email: String? = null,
    val designation: String? = null,
    val status: String? = null,
    val photo_url: String? = null
)

@Serializable
data class AttendanceDto(
    val id: String = "",
    val student_id: String = "",
    val date: String = "",
    val status: String = "present",
    val remarks: String? = null
)

@Serializable
data class BulkAttendanceRecordDto(
    val student_id: String,
    val status: String,
    val remarks: String? = null
)

@Serializable
data class BulkAttendanceRequest(
    val date: String,
    val records: List<BulkAttendanceRecordDto>
)

@Serializable
data class HifzLogDto(
    val id: String = "",
    val student_id: String = "",
    val date: String = "",
    val sabak: String? = null,
    val sabqi: String? = null,
    val manzil: String? = null,
    val status: String? = null,
    val remarks: String? = null
)

@Serializable
data class CreateHifzLogRequest(
    val student_id: String,
    val date: String,
    val sabak: String,
    val sabqi: String? = null,
    val manzil: String? = null,
    val status: String = "completed"
)

@Serializable
data class KitabLogDto(
    val id: String = "",
    val student_id: String = "",
    val date: String = "",
    val kitab_name: String? = null,
    val page_from: Int? = null,
    val page_to: Int? = null,
    val remarks: String? = null
)

@Serializable
data class CreateKitabLogRequest(
    val student_id: String,
    val date: String,
    val kitab_name: String,
    val page_from: Int? = null,
    val page_to: Int? = null,
    val remarks: String? = null
)

@Serializable
data class NooraniProgressDto(
    val id: String = "",
    val student_id: String = "",
    val lesson: String? = null,
    val status: String? = null,
    val updated_at: String? = null
)
