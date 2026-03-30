package com.izubot.treinemais.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.izubot.treinemais.data.local.SessionManager
import com.izubot.treinemais.ui.home.Home
import kotlinx.coroutines.flow.collectLatest

@Composable
@Preview
fun AppNavigation(
    onSessionExpired: () -> Unit = {},
    sessionManager: SessionManager = SessionManager(),
    navController: NavHostController = rememberNavController()
) {
    LaunchedEffect(Unit) {
        sessionManager.sessionExpired.collectLatest {
            onSessionExpired()
        }
    }

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                currentDestination = currentDestination,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainRoute.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<MainRoute.Home> {
                Home()
            }
        }
    }
}

@Composable
private fun AppBottomNavigation(
    currentDestination: NavDestination?,
    onNavigate: (Any) -> Unit
) {
    NavigationBar(
        modifier = Modifier.clip(
            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
    ) {
        AppBottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(item.route::class)
                } == true,
                onClick = { onNavigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                ),
            )
        }
    }
}

enum class AppBottomNavItem(
    val route: Any,
    val icon: ImageVector,
    val label: String
) {
    HOME(MainRoute.Home, Icons.Default.Home, "Home"),
    TRAINING("TESTE", Icons.Default.FitnessCenter, "Training"),
}