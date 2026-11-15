package com.qawmierp.apk.core.network

import com.qawmierp.apk.core.network.dto.RefreshDataDto
import com.qawmierp.apk.core.network.dto.RefreshRequest
import com.qawmierp.apk.core.storage.SessionManager
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val json: Json,
    private val bareClient: OkHttpClient
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Avoid infinite retry loops
        if (responseCount(response) >= 2) {
            sessionManager.clearSession(expired = true)
            return null
        }

        synchronized(lock) {
            val currentAccess = sessionManager.getAccessToken()
            val requestAuth = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

            // Another thread already refreshed the token
            if (!currentAccess.isNullOrBlank() && currentAccess != requestAuth) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccess")
                    .build()
            }

            val refreshToken = sessionManager.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                sessionManager.clearSession(expired = true)
                return null
            }

            val refreshUrl = "${sessionManager.getBaseUrl()}auth/refresh"
            val bodyJson = json.encodeToString(RefreshRequest.serializer(), RefreshRequest(refreshToken))
            val refreshReq = Request.Builder()
                .url(refreshUrl)
                .header("x-api-key", sessionManager.getApiKey())
                .header("Content-Type", "application/json")
                .post(bodyJson.toRequestBody("application/json".toMediaType()))
                .build()

            val refreshResp = runCatching { bareClient.newCall(refreshReq).execute() }.getOrNull()
            if (refreshResp == null || !refreshResp.isSuccessful) {
                refreshResp?.close()
                sessionManager.clearSession(expired = true)
                return null
            }

            val rawBody = refreshResp.body?.string()
            refreshResp.close()
            if (rawBody.isNullOrBlank()) {
                sessionManager.clearSession(expired = true)
                return null
            }

            val parsed = runCatching {
                json.decodeFromString(
                    ApiResponse.serializer(RefreshDataDto.serializer()),
                    rawBody
                )
            }.getOrNull()

            val newTokens = parsed?.data
            if (parsed?.success != true || newTokens == null) {
                sessionManager.clearSession(expired = true)
                return null
            }

            sessionManager.updateTokens(newTokens.access_token, newTokens.refresh_token)
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.access_token}")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
