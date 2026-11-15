package com.qawmierp.apk.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val madrasa_name: String,
    val full_name: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String
)

@Serializable
data class RefreshRequest(
    val refresh_token: String
)

@Serializable
data class AuthDataDto(
    val access_token: String,
    val refresh_token: String,
    val token_type: String = "Bearer",
    val expires_in: Long = 3600,
    val expires_at: Long = 0,
    val user: UserDto,
    val madrasa: MadrasaDto? = null
)

@Serializable
data class RefreshDataDto(
    val access_token: String,
    val refresh_token: String,
    val token_type: String = "Bearer",
    val expires_in: Long = 3600,
    val expires_at: Long = 0
)

@Serializable
data class MeDataDto(
    val user: UserDto,
    val madrasa: MadrasaDto? = null
)

@Serializable
data class LogoutDataDto(
    val logged_out: Boolean = true
)

@Serializable
data class UserDto(
    val id: String,
    val email: String? = null,
    val full_name: String = "",
    val phone: String? = null,
    val role: String = "student",
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList(),
    val madrasa_id: String? = null,
    val student_id_code: String? = null,
    val is_default_password: Boolean = false
)

@Serializable
data class MadrasaDto(
    val id: String,
    val name: String = "",
    val address: String? = null,
    val contact_phone: String? = null,
    val contact_email: String? = null,
    val logo_url: String? = null
)
