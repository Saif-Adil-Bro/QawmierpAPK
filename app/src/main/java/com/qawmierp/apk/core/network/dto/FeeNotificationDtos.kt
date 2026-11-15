package com.qawmierp.apk.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentFeeDto(
    val id: String = "",
    val student_id: String? = null,
    val title: String? = null,
    val amount: Double = 0.0,
    val paid_amount: Double = 0.0,
    val due_date: String? = null,
    val status: String = "unpaid"
)

@Serializable
data class FeePaymentDto(
    val id: String = "",
    val student_id: String? = null,
    val student_fee_id: String? = null,
    val amount: Double = 0.0,
    val payment_date: String? = null,
    val payment_method: String? = null
)

@Serializable
data class NotificationDto(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val is_read: Boolean = false,
    val created_at: String? = null
)

@Serializable
data class MarkNotificationRequest(
    val notification_id: String? = null,
    val mark_all: Boolean? = null
)

@Serializable
data class SettingsDataDto(
    val madrasa: MadrasaDto? = null,
    val settings: Map<String, String> = emptyMap()
)

@Serializable
data class UploadResponseDto(
    val url: String = ""
)
