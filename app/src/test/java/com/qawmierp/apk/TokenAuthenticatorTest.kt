package com.qawmierp.apk

import com.qawmierp.apk.core.network.TokenAuthenticator
import com.qawmierp.apk.core.storage.SessionManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class TokenAuthenticatorTest {

    private lateinit var server: MockWebServer
    private lateinit var sessionManager: SessionManager
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        sessionManager = mockk(relaxed = true)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `refreshes token on 401 and retries request with new bearer token`() {
        every { sessionManager.getAccessToken() } returns "old_access"
        every { sessionManager.getRefreshToken() } returns "valid_refresh"
        every { sessionManager.getBaseUrl() } returns server.url("/api/mobile/").toString()
        every { sessionManager.getApiKey() } returns "test-key"

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                      "success": true,
                      "data": {
                        "access_token": "new_access_123",
                        "refresh_token": "new_refresh_456",
                        "token_type": "Bearer",
                        "expires_in": 3600,
                        "expires_at": 1763160000
                      },
                      "message": "টোকেন রিফ্রেশ সফল হয়েছে।"
                    }
                    """.trimIndent()
                )
        )

        val authenticator = TokenAuthenticator(sessionManager, json, OkHttpClient())
        val failedResponse = okhttp3.Response.Builder()
            .request(
                Request.Builder()
                    .url(server.url("/api/mobile/students"))
                    .header("Authorization", "Bearer old_access")
                    .build()
            )
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        val retried = authenticator.authenticate(null, failedResponse)

        assertNotNull(retried)
        assertEquals("Bearer new_access_123", retried?.header("Authorization"))
        verify { sessionManager.updateTokens("new_access_123", "new_refresh_456") }
    }
}
