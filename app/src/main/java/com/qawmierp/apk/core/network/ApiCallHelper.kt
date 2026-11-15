package com.qawmierp.apk.core.network

import kotlinx.serialization.json.Json
import retrofit2.Response

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
                    json.decodeFromString(ApiResponse.serializer(kotlinx.serialization.builtins.serializer<Unit>()), errorRaw)
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
