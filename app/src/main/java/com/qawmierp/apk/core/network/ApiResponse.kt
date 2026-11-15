package com.qawmierp.apk.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val meta: PaginationMeta? = null,
    val message: String? = null,
    val code: String? = null
)

@Serializable
data class PaginationMeta(
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    val totalPages: Int = 1,
    val unread_count: Int? = null
)

sealed class ApiResult<out T> {
    data class Success<T>(
        val data: T,
        val meta: PaginationMeta? = null,
        val message: String? = null
    ) : ApiResult<T>()

    data class Error(
        val message: String,
        val code: String? = null,
        val httpCode: Int? = null
    ) : ApiResult<Nothing>()
}
