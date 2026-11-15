package com.qawmierp.apk

import com.qawmierp.apk.core.network.ApiResponse
import com.qawmierp.apk.core.network.dto.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase2DtosTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Test
    fun `parses exams and zakat funds list envelopes`() {
        val examsJson = """
            {
              "success": true,
              "data": [
                { "id": "ex-1", "name": "বার্ষিক পরীক্ষা ২০২৬", "exam_type": "annual", "start_date": "2026-12-01", "status": "scheduled" }
              ],
              "meta": { "page": 1, "limit": 20, "total": 1, "totalPages": 1 },
              "message": "Success"
            }
        """.trimIndent()

        val fundsJson = """
            {
              "success": true,
              "data": [
                { "id": "f-1", "name": "যাকাত ফান্ড", "fund_type": "zakat", "balance": 125000.0 }
              ],
              "message": "Success"
            }
        """.trimIndent()

        val exams = json.decodeFromString(ApiResponse.serializer(ListSerializer(ExamDto.serializer())), examsJson)
        val funds = json.decodeFromString(ApiResponse.serializer(ListSerializer(FundDto.serializer())), fundsJson)

        assertTrue(exams.success)
        assertEquals("বার্ষিক পরীক্ষা ২০২৬", exams.data?.first()?.name)
        assertTrue(funds.success)
        assertEquals(125000.0, funds.data?.first()?.balance ?: 0.0, 0.01)
    }
}
