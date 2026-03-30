package com.izubot.treinemais

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.izubot.treinemais.data.local.SessionManager
import com.izubot.treinemais.data.local.TokenManager
import com.izubot.treinemais.ui.navigation.RootNavigation
import com.izubot.treinemais.ui.theme.TreineMaisTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var sessionManager: SessionManager
    
    private var deepLinkIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deepLinkIntent = intent

        setContent {
//            val isLoggedIn by tokenManager.isLoggedIn.collectAsState(initial = false)

            val isLoggedIn by remember { mutableStateOf(true) }
            
            TreineMaisTheme {
                RootNavigation(
                    isLoggedIn = isLoggedIn,
                    sessionManager = sessionManager,
                    deepLinkIntent = deepLinkIntent
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkIntent = intent
    }
}