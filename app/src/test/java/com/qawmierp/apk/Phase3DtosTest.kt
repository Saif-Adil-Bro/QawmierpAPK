package com.qawmierp.apk

import com.qawmierp.apk.core.network.ApiResponse
import com.qawmierp.apk.core.network.dto.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase3DtosTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Test
    fun `parses library books and audit logs envelopes`() {
        val booksJson = """
            {
              "success": true,
              "data": [
                { "id": "b-1", "title": "সহীহ বুখারী", "author": "ইমাম বুখারী (রহ.)", "available_copies": 12, "total_copies": 15 }
              ],
              "message": "Success"
            }
        """.trimIndent()

        val auditJson = """
            {
              "success": true,
              "data": [
                { "id": "a-1", "actor_name": "Super Admin", "action": "CREATE", "entity": "students", "created_at": "2026-11-14T10:00:00Z" }
              ],
              "message": "Success"
            }
        """.trimIndent()

        val books = json.decodeFromString(ApiResponse.serializer(ListSerializer(LibraryBookDto.serializer())), booksJson)
        val logs = json.decodeFromString(ApiResponse.serializer(ListSerializer(AuditLogDto.serializer())), auditJson)

        assertTrue(books.success)
        assertEquals("সহীহ বুখারী", books.data?.first()?.title)
        assertTrue(logs.success)
        assertEquals("CREATE", logs.data?.first()?.action)
    }
}
