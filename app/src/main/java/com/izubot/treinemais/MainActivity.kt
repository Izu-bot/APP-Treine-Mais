package com.izubot.treinemais

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
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

    /**
     * Initializes the activity's UI and navigation when the activity is created.
     *
     * Enables edge-to-edge mode, determines whether a user is logged in by checking tokens,
     * creates and stores a NavHostController, applies the app theme, and sets up the Scaffold and
     * AppNavigation composable with the computed start destination, login state, and session manager.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isLoggedIn = tokenManager.getAccessToken() != null && tokenManager.getRefreshToken() != null

        setContent {
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

    /**
     * Handles a newly delivered intent by updating the activity's stored intent and delegating it to the navigation controller for deep-link processing.
     *
     * @param intent The new intent delivered to the activity; used to replace the activity's current intent and to trigger deep-link handling when a NavController is present.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.handleDeepLink(intent)
    }
}