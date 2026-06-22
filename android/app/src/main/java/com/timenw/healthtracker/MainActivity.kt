package com.timenw.healthtracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.timenw.healthtracker.data.model.*
import com.timenw.healthtracker.data.repository.HealthRepository
import com.timenw.healthtracker.notification.NotificationHelper
import com.timenw.healthtracker.ui.screens.*
import com.timenw.healthtracker.ui.theme.HealthTrackerTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private lateinit var repository: HealthRepository
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = HealthRepository(applicationContext)
        NotificationHelper.createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        setContent { HealthTrackerTheme { MainScreen(repository) } }
    }
}

sealed class Screen(val route: String, val label: String, val selectedIcon: @Composable () -> Unit, val unselectedIcon: @Composable () -> Unit) {
    object Home : Screen("home", "健康", { Icon(Icons.Filled.Favorite, contentDescription = null) }, { Icon(Icons.Outlined.Favorite, contentDescription = null) })
    object Stats : Screen("stats", "统计", { Icon(Icons.Filled.BarChart, contentDescription = null) }, { Icon(Icons.Outlined.BarChart, contentDescription = null) })
    object Settings : Screen("settings", "设置", { Icon(Icons.Filled.Settings, contentDescription = null) }, { Icon(Icons.Outlined.Settings, contentDescription = null) })
}

@Composable
fun MainScreen(repository: HealthRepository) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Stats, Screen.Settings)
    var settings by remember { mutableStateOf(repository.getSettings()) }
    val today = remember { LocalDate.now() }
    var summary by remember { mutableStateOf(repository.getDailySummary(today)) }
    var records by remember { mutableStateOf(repository.getHealthRecords(today)) }
    var weeklyData by remember { mutableStateOf(repository.getWeeklyData()) }

    fun refreshData() {
        summary = repository.getDailySummary(today); records = repository.getHealthRecords(today); weeklyData = repository.getWeeklyData()
    }

    Scaffold(bottomBar = {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = { if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) screen.selectedIcon() else screen.unselectedIcon() },
                    label = { Text(screen.label) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = { navController.navigate(screen.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } }
                )
            }
        }
    }) { innerPadding ->
        NavHost(navController = navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                HealthHomeTab(summary = summary, records = records,
                    onAddRecord = { record -> repository.addHealthRecord(record); refreshData() },
                    onRemoveRecord = { id -> repository.removeHealthRecord(id, today); refreshData() })
            }
            composable(Screen.Stats.route) { StatsTab(weeklyData = weeklyData) }
            composable(Screen.Settings.route) {
                SettingsTab(settings = settings, onSettingsChanged = { newSettings -> repository.saveSettings(newSettings); settings = newSettings })
            }
        }
    }
}
