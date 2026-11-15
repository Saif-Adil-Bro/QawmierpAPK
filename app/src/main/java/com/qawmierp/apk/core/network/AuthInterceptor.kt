package com.qawmierp.apk.core.network

import com.qawmierp.apk.core.storage.SessionManager
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Dynamically rewrite host/scheme/base-path if user configured custom Server URL
        val configuredBase = sessionManager.getBaseUrl().toHttpUrlOrNull()
        val rewrittenUrl = if (configuredBase != null && original.url.host == PLACEHOLDER_HOST) {
            val relativePath = original.url.encodedPath.removePrefix("/")
            val basePath = configuredBase.encodedPath.trimEnd('/')
            original.url.newBuilder()
                .scheme(configuredBase.scheme)
                .host(configuredBase.host)
                .port(configuredBase.port)
                .encodedPath("$basePath/$relativePath")
                .build()
        } else {
            original.url
        }

        val requestBuilder = original.newBuilder()
            .url(rewrittenUrl)
            .header("x-api-key", sessionManager.getApiKey())
            .header("Accept", "application/json")

        val accessToken = sessionManager.getAccessToken()
        val isAuthPublicEndpoint = rewrittenUrl.encodedPath.endsWith("/auth/login") ||
            rewrittenUrl.encodedPath.endsWith("/auth/register") ||
            rewrittenUrl.encodedPath.endsWith("/auth/refresh")

        if (!accessToken.isNullOrBlank() && !isAuthPublicEndpoint) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        const val PLACEHOLDER_HOST = "qawmierp.placeholder.local"
    }
}
