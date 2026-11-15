package com.qawmierp.apk.core.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.Response

@Serializable
private data class ErrorEnvelope(
    val success: Boolean = false,
    val message: String? = null,
    val code: String? = null
)

suspend fun <T> safeApiCall(
    json: Json,
    call: suspend () -> Response<ApiResponse<T>>
): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.success && body.data != null) {
                ApiResult.Success(
                    data = body.data,
                    meta = body.meta,
                    message = body.message
                )
            } else {
                ApiResult.Error(
                    message = body?.message ?: "Unknown server response",
                    code = body?.code,
                    httpCode = response.code()
                )
            }
        } else {
            val errorRaw = response.errorBody()?.string()
            val parsedError = if (!errorRaw.isNullOrBlank()) {
                runCatching {
                    json.decodeFromString(ErrorEnvelope.serializer(), errorRaw)
                }.getOrNull()
            } else null

            ApiResult.Error(
                message = parsedError?.message ?: "HTTP ${response.code()} Error",
                code = parsedError?.code,
                httpCode = response.code()
            )
        }
    } catch (e: Exception) {
        ApiResult.Error(
            message = e.localizedMessage ?: "Network error"
        )
    }
}
