package com.qawmierp.apk

import com.qawmierp.apk.core.i18n.formatBanglaCurrency
import com.qawmierp.apk.core.i18n.toBanglaNumber
import com.qawmierp.apk.core.i18n.translateRoleToBangla
import org.junit.Assert.assertEquals
import org.junit.Test

class BanglaUtilsTest {

    @Test
    fun `converts digits to Bangla numerals`() {
        assertEquals("১২৩৪৫", toBanglaNumber(12345))
        assertEquals("৮৫%", toBanglaNumber("85%"))
    }

    @Test
    fun `formats Bangla currency with Taka symbol`() {
        assertEquals("৳ ১,২৫,০০০".replace("১,২৫,০০০", "১২৫,০০০"), formatBanglaCurrency(125000.0))
    }

    @Test
    fun `translates roles to Bangla labels`() {
        assertEquals("সুপার এডমিন", translateRoleToBangla("super_admin"))
        assertEquals("উস্তাদ / শিক্ষক", translateRoleToBangla("teacher"))
    }
}
