package com.qawmierp.apk

import com.qawmierp.apk.core.network.dto.UserDto
import com.qawmierp.apk.ui.navigation.Routes
import com.qawmierp.apk.ui.navigation.resolveHomeRouteForUser
import org.junit.Assert.assertEquals
import org.junit.Test

class RoleRouterTest {

    @Test
    fun `routes super_admin to AdminDashboard`() {
        val user = UserDto(id = "1", role = "super_admin")
        assertEquals(Routes.ADMIN_DASHBOARD, resolveHomeRouteForUser(user))
    }

    @Test
    fun `routes teacher to TeacherPortal`() {
        val user = UserDto(id = "2", role = "teacher")
        assertEquals(Routes.TEACHER_PORTAL, resolveHomeRouteForUser(user))
    }

    @Test
    fun `routes student or parent to StudentPortal`() {
        val student = UserDto(id = "3", role = "student")
        val parent = UserDto(id = "4", role = "parent")
        assertEquals(Routes.STUDENT_PORTAL, resolveHomeRouteForUser(student))
        assertEquals(Routes.STUDENT_PORTAL, resolveHomeRouteForUser(parent))
    }
}
