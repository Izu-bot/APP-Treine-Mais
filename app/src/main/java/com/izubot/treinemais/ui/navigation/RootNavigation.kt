package com.izubot.treinemais.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.izubot.treinemais.data.local.helpers.SessionManager
import com.izubot.treinemais.ui.splash.Splash
import com.izubot.treinemais.utils.FocusAction
import com.izubot.treinemais.utils.FocusManager
import com.izubot.treinemais.utils.FocusManagerViewModel
import kotlinx.serialization.Serializable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Serializable sealed class RootRoute {
    @Serializable data object Splash : RootRoute()
    @Serializable data object Auth : RootRoute()
    @Serializable data object Main : RootRoute()
}

@Composable
fun RootNavigation(
    isLoggedIn: Boolean?,
    sessionManager: SessionManager,
    deepLinkIntent: Intent? = null,
    navController: NavHostController = rememberNavController(),
    focusManager: FocusManager = hiltViewModel<FocusManagerViewModel>().focusManager
) {
    val localFocusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusManager.focusActions.collect { action ->
            when (action) {
                is FocusAction.Clear -> localFocusManager.clearFocus()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = RootRoute.Splash
    ) {
        composable<RootRoute.Splash> {
            Scaffold { innerPadding ->
                Splash(
                    onSplashFinished = {
                        if (isLoggedIn != null) {
                            val destination = if (isLoggedIn) RootRoute.Main else RootRoute.Auth
                            navController.navigate(destination) {
                                popUpTo(RootRoute.Splash) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.padding(innerPadding),
                    isLoggedIn = isLoggedIn
                )
            }
        }

        composable<RootRoute.Auth> {
            AuthNavigation(
                deepLinkIntent = deepLinkIntent,
                onLoginSuccess = {
                    navController.navigate(RootRoute.Main) {
                        popUpTo(RootRoute.Auth) { inclusive = true }
                    }
                }
            )
        }

        composable<RootRoute.Main> {
            AppNavigation(
                sessionManager = sessionManager,
                onSessionExpired = {
                    navController.navigate(RootRoute.Auth) {
                        popUpTo(RootRoute.Main) { inclusive = true }
                    }
                }
            )
        }
    }
}