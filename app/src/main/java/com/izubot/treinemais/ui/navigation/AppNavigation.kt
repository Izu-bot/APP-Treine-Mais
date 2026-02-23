package com.izubot.treinemais.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.izubot.treinemais.ui.login.Login
import com.izubot.treinemais.ui.register.Register
import com.izubot.treinemais.ui.splash.Splash
import com.izubot.treinemais.ui.welcome.Welcome

@Composable
fun AppNavigation(
    paddingValues: PaddingValues,
    navController: NavHostController = rememberNavController()
) {

    NavHost (
        navController = navController,
        startDestination = AuthRoute.Splash,
    ) {
        composable<AuthRoute.Splash> {
            Splash(
                onSplashFinished = {
                    navController.navigate(AuthRoute.Welcome) {
                        popUpTo(AuthRoute.Splash) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .padding(paddingValues)
            )
        }

        composable<AuthRoute.Welcome> {
            Welcome(
                onNavigateToLogin = {
                    navController.navigate(AuthRoute.Login)
                },
                onNavigateToRegister = {
                    navController.navigate(AuthRoute.Register)
                },
                modifier = Modifier
                    .padding(paddingValues)
            )
        }

        composable<AuthRoute.Login> {
            Login(
                onNavigateToWelcome = {
                    navController.popBackStack()
                },
                onLoginSuccess = {},
                modifier = Modifier
                    .padding(paddingValues)
            )
        }

        composable<AuthRoute.Register> {
            Register(
                onNavigateBack = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(paddingValues)
            )
        }
    }
}