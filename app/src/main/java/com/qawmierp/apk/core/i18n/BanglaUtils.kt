package com.qawmierp.apk.core.i18n

import java.text.NumberFormat
import java.util.Locale

private val BANGLA_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

fun toBanglaNumber(input: Any?): String {
    val str = input?.toString() ?: return "০"
    val sb = StringBuilder(str.length)
    for (ch in str) {
        if (ch in '0'..'9') {
            sb.append(BANGLA_DIGITS[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

fun formatBanglaCurrency(amount: Double): String {
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(amount.toLong())
    return "৳ ${toBanglaNumber(formatted)}"
}

fun translateRoleToBangla(role: String?): String {
    return when (role?.lowercase()) {
        "super_admin" -> "সুপার এডমিন"
        "admin" -> "এডমিন"
        "muhtamim" -> "মুহতামিম"
        "accountant" -> "হিসাবরক্ষক"
        "teacher", "ustad" -> "উস্তাদ / শিক্ষক"
        "student" -> "শিক্ষার্থী"
        "parent", "guardian" -> "অভিভাবক"
        else -> role ?: "সদস্য"
    }
}
