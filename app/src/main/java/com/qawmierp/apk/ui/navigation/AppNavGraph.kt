package com.qawmierp.apk.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.qawmierp.apk.ui.academic.AttendanceScreen
import com.qawmierp.apk.ui.academic.HifzKitabScreen
import com.qawmierp.apk.ui.academic.StudentsScreen
import com.qawmierp.apk.ui.auth.AuthViewModel
import com.qawmierp.apk.ui.auth.BrandedSplashScreen
import com.qawmierp.apk.ui.auth.LoginScreen
import com.qawmierp.apk.ui.auth.RegisterScreen
import com.qawmierp.apk.ui.dashboard.AdminDashboardScreen
import com.qawmierp.apk.ui.exams.ExamsScreen
import com.qawmierp.apk.ui.finance.FinanceScreen
import com.qawmierp.apk.ui.operations.OperationsScreen
import com.qawmierp.apk.ui.portal.StudentPortalScreen
import com.qawmierp.apk.ui.portal.TeacherPortalScreen
import com.qawmierp.apk.ui.utility.FeesScreen
import com.qawmierp.apk.ui.utility.NotificationsScreen
import com.qawmierp.apk.ui.utility.SettingsScreen

private data class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavGraph(sessionExpired: Boolean, userRole: String? = null) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    var activeRole by remember(userRole) { mutableStateOf(userRole) }

    LaunchedEffect(sessionExpired) {
        if (sessionExpired) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val showBottomBar = currentRoute != null &&
        currentRoute !in setOf(Routes.SPLASH, Routes.LOGIN, Routes.REGISTER)

    val bottomTabs = remember(activeRole, currentRoute) {
        val role = activeRole?.lowercase()
        when {
            role in setOf("teacher", "ustad") || currentRoute == Routes.TEACHER_PORTAL -> listOf(
                BottomNavTab(Routes.TEACHER_PORTAL, "পোর্টাল", Icons.Default.Home),
                BottomNavTab(Routes.ATTENDANCE, "হাজিরা", Icons.Default.FactCheck),
                BottomNavTab(Routes.HIFZ_KITAB, "সবক", Icons.Default.MenuBook),
                BottomNavTab(Routes.SETTINGS, "সেটিংস", Icons.Default.Settings)
            )
            role in setOf("student", "parent", "guardian") || currentRoute == Routes.STUDENT_PORTAL -> listOf(
                BottomNavTab(Routes.STUDENT_PORTAL, "পোর্টাল", Icons.Default.Home),
                BottomNavTab(Routes.FEES, "ফি", Icons.Default.Payments),
                BottomNavTab(Routes.EXAMS, "পরীক্ষা", Icons.Default.Assignment),
                BottomNavTab(Routes.SETTINGS, "সেটিংস", Icons.Default.Settings)
            )
            else -> listOf(
                BottomNavTab(Routes.ADMIN_DASHBOARD, "হোম", Icons.Default.Home),
                BottomNavTab(Routes.STUDENTS, "ছাত্র", Icons.Default.People),
                BottomNavTab(Routes.ATTENDANCE, "হাজিরা", Icons.Default.FactCheck),
                BottomNavTab(Routes.FINANCE, "হিসাব", Icons.Default.AccountBalance),
                BottomNavTab(Routes.SETTINGS, "সেটিংস", Icons.Default.Settings)
            )
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.SPLASH) {
                val authViewModel: AuthViewModel = hiltViewModel()
                val state by authViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    authViewModel.checkExistingSession()
                }
                LaunchedEffect(state.targetRoute) {
                    state.targetRoute?.let { route ->
                        activeRole = state.currentUser?.role
                        navController.navigate(route) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
                BrandedSplashScreen()
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateToRoute = { route ->
                        activeRole = when (route) {
                            Routes.TEACHER_PORTAL -> "teacher"
                            Routes.STUDENT_PORTAL -> "student"
                            else -> "super_admin"
                        }
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
                        activeRole = "super_admin"
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
            composable(Routes.EXAMS) {
                ExamsScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.FINANCE) {
                FinanceScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.OPERATIONS) {
                OperationsScreen(onBack = { navController.popBackStack() })
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
}
