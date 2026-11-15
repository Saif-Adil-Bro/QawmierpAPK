package com.qawmierp.apk.core.network.dto

import kotlinx.serialization.Serializable

// --- Boarding & Bazar ---
@Serializable
data class BoardingMealDto(
    val id: String = "",
    val date: String = "",
    val meal_type: String? = null,
    val total_students: Int = 0,
    val cost: Double = 0.0
)

@Serializable
data class BoardingBazarDto(
    val id: String = "",
    val date: String = "",
    val item_name: String = "",
    val quantity: String? = null,
    val total_price: Double = 0.0
)

// --- Library ---
@Serializable
data class LibraryBookDto(
    val id: String = "",
    val title: String = "",
    val author: String? = null,
    val available_copies: Int = 0,
    val total_copies: Int = 0
)

@Serializable
data class BookIssueDto(
    val id: String = "",
    val book_title: String? = null,
    val student_name: String? = null,
    val issue_date: String? = null,
    val due_date: String? = null,
    val status: String = "issued"
)

// --- Inventory & Transport ---
@Serializable
data class InventoryItemDto(
    val id: String = "",
    val item_name: String = "",
    val category: String? = null,
    val quantity: Int = 0,
    val unit: String? = null
)

@Serializable
data class TransportDto(
    val id: String = "",
    val vehicle_name: String = "",
    val route_name: String? = null,
    val driver_name: String? = null,
    val driver_phone: String? = null
)

// --- Communication & Notices ---
@Serializable
data class CreateNoticeRequest(
    val title: String,
    val content: String
)

@Serializable
data class SmsLogDto(
    val id: String = "",
    val recipient: String = "",
    val message: String = "",
    val status: String = "sent",
    val sent_at: String? = null
)

@Serializable
data class ParentCommunicationDto(
    val id: String = "",
    val student_name: String? = null,
    val subject: String = "",
    val message: String = "",
    val created_at: String? = null
)

// --- Tarbiyah, Child Safety & Biometric ---
@Serializable
data class TarbiyahBadgeDto(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val points: Int = 0
)

@Serializable
data class AmalTrackerDto(
    val id: String = "",
    val student_name: String? = null,
    val date: String = "",
    val salah_count: Int = 0,
    val tilawat_minutes: Int = 0
)

@Serializable
data class ChildSafetyDto(
    val id: String = "",
    val title: String = "",
    val status: String = "active",
    val reported_at: String? = null
)

@Serializable
data class BiometricDeviceDto(
    val id: String = "",
    val device_name: String = "",
    val serial_number: String? = null,
    val status: String = "online"
)

// --- Users, Roles & Security Audit Logs ---
@Serializable
data class RoleDto(
    val id: String = "",
    val name: String = "",
    val permissions: List<String> = emptyList()
)

@Serializable
data class AuditLogDto(
    val id: String = "",
    val actor_name: String? = null,
    val action: String = "",
    val entity: String? = null,
    val created_at: String? = null
)
