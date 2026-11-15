package com.qawmierp.apk

import com.qawmierp.apk.core.network.ApiResponse
import com.qawmierp.apk.core.network.dto.StudentDto
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ApiResponseTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Test
    fun `parses paginated student list envelope from MOBILE_API_DOCS`() {
        val raw = """
            {
              "success": true,
              "data": [
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "madrasa_id": "550e8400-e29b-41d4-a716-446655440001",
                  "full_name": "আব্দুল্লাহ আল মামুন",
                  "roll_number": "101",
                  "student_id_code": "AHH480101",
                  "class_id": "class-uuid",
                  "guardian_name": "মোঃ আব্দুর রহমান",
                  "parent_phone": "01711000111",
                  "photo_url": "https://your-domain.com/uploads/student_101.jpg",
                  "classes": {
                    "id": "class-uuid",
                    "name": "জালালাইন"
                  }
                }
              ],
              "meta": {
                "page": 1,
                "limit": 20,
                "total": 1,
                "totalPages": 1
              },
              "message": "Success"
            }
        """.trimIndent()

        val parsed = json.decodeFromString(
            ApiResponse.serializer(ListSerializer(StudentDto.serializer())),
            raw
        )

        assertTrue(parsed.success)
        assertEquals(1, parsed.data?.size)
        assertEquals("আব্দুল্লাহ আল মামুন", parsed.data?.first()?.full_name)
        assertEquals("জালালাইন", parsed.data?.first()?.classes?.name)
        assertEquals(1, parsed.meta?.total)
    }

    @Test
    fun `parses error envelope with INVALID_API_KEY`() {
        val raw = """
            {
              "success": false,
              "data": null,
              "message": "অবৈধ বা অনুপস্থিত API Key (x-api-key)।",
              "code": "INVALID_API_KEY"
            }
        """.trimIndent()

        val parsed = json.decodeFromString(
            ApiResponse.serializer(StudentDto.serializer()),
            raw
        )

        assertFalse(parsed.success)
        assertNull(parsed.data)
        assertEquals("INVALID_API_KEY", parsed.code)
    }
}
