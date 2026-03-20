package com.izubot.treinemais

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.izubot.treinemais.data.local.SessionManager
import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.ui.navigation.AppNavigation
import com.izubot.treinemais.ui.navigation.AuthRoute
import com.izubot.treinemais.ui.theme.TreineMaisTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var sessionManager: SessionManager
    
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isLoggedIn by tokenManager.isLoggedIn.collectAsState(initial = false)

            val controller = rememberNavController()
            navController = controller
            
            TreineMaisTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        paddingValues = innerPadding,
                        navController = controller,
                        startDestination = AuthRoute.Splash,
                        isLoggedIn = isLoggedIn,
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.handleDeepLink(intent)
    }
}