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
import com.izubot.treinemais.ui.onboarding.Onboarding
import com.izubot.treinemais.data.local.datasource.DataStorePrefs
import com.izubot.treinemais.utils.FocusAction
import com.izubot.treinemais.utils.FocusManager
import kotlinx.serialization.Serializable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Serializable sealed class RootRoute {
    @Serializable data object Splash : RootRoute()
    @Serializable data object Auth : RootRoute()
    @Serializable data object Main : RootRoute()
    @Serializable data object Onboarding : RootRoute()
}

@Composable
fun RootNavigation(
    isLoggedIn: Boolean?,
    sessionManager: SessionManager,
    focusManager: FocusManager,
    dataStorePrefs: DataStorePrefs,
    deepLinkIntent: Intent? = null,
    navController: NavHostController = rememberNavController(),
) {
    val localFocusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val hasCompletedOnboarding by dataStorePrefs.hasCompletedOnboarding.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        focusManager.focusActions.collect { action ->
            when (action) {
                is FocusAction.Clear -> localFocusManager.clearFocus()
            }
        }
    }

    val hasCompletedOnboardingValue = hasCompletedOnboarding

    NavHost(
        navController = navController,
        startDestination = RootRoute.Splash
    ) {
        composable<RootRoute.Splash> {
            Scaffold { innerPadding ->
                Splash(
                    onSplashFinished = {
                        if (isLoggedIn != null && hasCompletedOnboardingValue != null) {
                            val destination = when {
                                !isLoggedIn -> RootRoute.Auth
                                !hasCompletedOnboardingValue -> RootRoute.Onboarding
                                else -> RootRoute.Main
                            }
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

        composable<RootRoute.Onboarding> {
            Onboarding(
                onGetStarted = {
                    scope.launch {
                        dataStorePrefs.saveOnboardingCompleted()
                        navController.navigate(RootRoute.Main) {
                            popUpTo(RootRoute.Onboarding) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<RootRoute.Auth> {
            AuthNavigation(
                deepLinkIntent = deepLinkIntent,
                onLoginSuccess = {
                    if (hasCompletedOnboardingValue == false) {
                        navController.navigate(RootRoute.Onboarding) {
                            popUpTo(RootRoute.Auth) { inclusive = true }
                        }
                    } else {
                        navController.navigate(RootRoute.Main) {
                            popUpTo(RootRoute.Auth) { inclusive = true }
                        }
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