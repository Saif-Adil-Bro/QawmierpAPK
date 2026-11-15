package com.qawmierp.apk.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qawmierp.apk.ui.academic.AttendanceScreen
import com.qawmierp.apk.ui.academic.HifzKitabScreen
import com.qawmierp.apk.ui.academic.StudentsScreen
import com.qawmierp.apk.ui.auth.AuthViewModel
import com.qawmierp.apk.ui.auth.LoginScreen
import com.qawmierp.apk.ui.auth.RegisterScreen
import com.qawmierp.apk.ui.dashboard.AdminDashboardScreen
import com.qawmierp.apk.ui.portal.StudentPortalScreen
import com.qawmierp.apk.ui.portal.TeacherPortalScreen
import com.qawmierp.apk.ui.utility.FeesScreen
import com.qawmierp.apk.ui.utility.NotificationsScreen
import com.qawmierp.apk.ui.utility.SettingsScreen

@Composable
fun AppNavGraph(sessionExpired: Boolean) {
    val navController = rememberNavController()

    LaunchedEffect(sessionExpired) {
        if (sessionExpired) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val state by authViewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                authViewModel.checkExistingSession()
            }
            LaunchedEffect(state.targetRoute) {
                state.targetRoute?.let { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRoute = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToRoute = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(onNavigate = { navController.navigate(it) })
        }
        composable(Routes.TEACHER_PORTAL) {
            TeacherPortalScreen(onNavigate = { navController.navigate(it) })
        }
        composable(Routes.STUDENT_PORTAL) {
            StudentPortalScreen(onNavigate = { navController.navigate(it) })
        }
        composable(Routes.STUDENTS) {
            StudentsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ATTENDANCE) {
            AttendanceScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.HIFZ_KITAB) {
            HifzKitabScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.FEES) {
            FeesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
