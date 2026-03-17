package com.izubot.treinemais.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.izubot.treinemais.data.local.SessionManager
import com.izubot.treinemais.ui.confirm.Confirm
import com.izubot.treinemais.ui.login.Login
import com.izubot.treinemais.ui.register.Register
import com.izubot.treinemais.ui.splash.Splash
import com.izubot.treinemais.ui.welcome.Welcome
import kotlinx.coroutines.flow.collectLatest

/**
 * Hosts the app's navigation graph for authentication flows and reacts to session state.
 *
 * The composable defines routes for Splash, Welcome, Login (with confirm-email deep link), Register and Confirm,
 * applies the provided padding to each destination, and performs navigation transitions (including clearing
 * back stack when appropriate). It also observes session expiration and navigates to the Welcome route clearing
 * the entire back stack when a session expires.
 *
 * @param paddingValues Insets to apply to destination screens.
 * @param startDestination The initial navigation route to show when the NavHost is created.
 * @param isLoggedIn Whether the user is currently authenticated; used to decide post-splash navigation.
 * @param sessionManager Provides session events (e.g., session expiration) that drive global navigation reactions.
 * @param navController Controller used for navigation; a default is provided by rememberNavController().
 */
@Composable
fun AppNavigation(
    paddingValues: PaddingValues,
    startDestination: AuthRoute,
    isLoggedIn: Boolean,
    sessionManager: SessionManager,
    navController: NavHostController = rememberNavController()
) {

    // Escuta eventos de sessão expirada do TokenAuthenticator
    LaunchedEffect(Unit) {
        sessionManager.sessionExpired.collectLatest {
            navController.navigate(AuthRoute.Welcome) {
                // Limpa todo o histórico de navegação
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost (
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<AuthRoute.Splash> {
            Splash(
                onSplashFinished = {
                    if (isLoggedIn) {
                        // TODO: Quando tiver a HomeRoute implementada
                        // navController.navigate(AuthRoute.Home) { ... }
                        navController.navigate(AuthRoute.Welcome) {
                            popUpTo(AuthRoute.Splash) { inclusive = true }
                        }
                    } else {
                        navController.navigate(AuthRoute.Welcome) {
                            popUpTo(AuthRoute.Splash) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .padding(paddingValues)
            )
        }

        composable<AuthRoute.Welcome> {
            Welcome(
                onNavigateToLogin = {
                    navController.navigate(AuthRoute.Login())
                },
                onNavigateToRegister = {
                    navController.navigate(AuthRoute.Register)
                },
                modifier = Modifier
                    .padding(paddingValues)
            )
        }

        composable<AuthRoute.Login>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "http://localhost:5297/auth/confirm-email?token={token}"
                }
            )
        ) { backStackEntry ->
            val loginRoute: AuthRoute.Login = backStackEntry.toRoute()
            Login(
                token = loginRoute.token,
                onNavigateToWelcome = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    // Após login com sucesso, navega para a Home
                    // navController.navigate(AuthRoute.Home) { ... }
                },
                modifier = Modifier
                    .padding(paddingValues)
            )
        }

        composable<AuthRoute.Register> {
            Register(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToConfirm = {
                    navController.navigate(AuthRoute.Confirm)
                },
                modifier = Modifier
                    .padding(paddingValues)
            )
        }

        composable<AuthRoute.Confirm> {
            Confirm(
                modifier = Modifier
                    .padding(paddingValues)
            )
        }
    }
}
