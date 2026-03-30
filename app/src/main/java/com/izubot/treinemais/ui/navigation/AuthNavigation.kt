package com.izubot.treinemais.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.izubot.treinemais.ui.confirm.Confirm
import com.izubot.treinemais.ui.login.Login
import com.izubot.treinemais.ui.register.Register
import com.izubot.treinemais.ui.welcome.Welcome

@Composable
fun AuthNavigation(
    navController: NavHostController = rememberNavController(),
    onLoginSuccess: () -> Unit,
    deepLinkIntent: Intent? = null
) {
    LaunchedEffect(deepLinkIntent) {
        deepLinkIntent?.let { navController.handleDeepLink(it) }
    }

    NavHost(
        navController = navController,
        startDestination = AuthRoute.Welcome
    ) {
        composable<AuthRoute.Welcome> {
            Welcome(
                onNavigateToLogin = { navController.navigate(AuthRoute.Login) },
                onNavigateToRegister = { navController.navigate(AuthRoute.Register) }
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
                onNavigateToWelcome = { navController.popBackStack() },
                onLoginSuccess = onLoginSuccess
            )
        }

        composable<AuthRoute.Register> {
            Register(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToConfirm = { navController.navigate(AuthRoute.Confirm) }
            )
        }

        composable<AuthRoute.Confirm> {
            Confirm()
        }
    }
}