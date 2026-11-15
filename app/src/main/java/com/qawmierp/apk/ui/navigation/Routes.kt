package com.qawmierp.apk.ui.navigation

import com.qawmierp.apk.core.network.dto.UserDto

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val TEACHER_PORTAL = "teacher_portal"
    const val STUDENT_PORTAL = "student_portal"
    const val STUDENTS = "students"
    const val ATTENDANCE = "attendance"
    const val HIFZ_KITAB = "hifz_kitab"
    const val FEES = "fees"
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"
}

fun resolveHomeRouteForUser(user: UserDto?): String {
    if (user == null) return Routes.LOGIN
    val role = user.role.lowercase()
    return when {
        role in setOf("super_admin", "admin", "muhtamim", "accountant", "staff") -> Routes.ADMIN_DASHBOARD
        role in setOf("teacher", "ustad") -> Routes.TEACHER_PORTAL
        else -> Routes.STUDENT_PORTAL
    }
}
