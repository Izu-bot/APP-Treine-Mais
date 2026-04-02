package com.izubot.treinemais

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.izubot.treinemais.data.local.helpers.SessionManager
import com.izubot.treinemais.data.local.datasource.DataStorePrefs
import com.izubot.treinemais.ui.navigation.RootNavigation
import com.izubot.treinemais.ui.theme.TreineMaisTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var dataStorePrefs: DataStorePrefs
    @Inject
    lateinit var sessionManager: SessionManager
    
    private var deepLinkIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deepLinkIntent = intent

        setContent {
            val isLoggedIn by dataStorePrefs.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

            val isDynamicTheme by dataStorePrefs.getThemePref.collectAsStateWithLifecycle(initialValue = false)

//            val isLoggedIn by remember { mutableStateOf(true) }
            
            TreineMaisTheme(
                dynamicColor = isDynamicTheme
            ) {
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
        Log.d("DeepLink", "Novo Intent: ${intent.dataString}")
        deepLinkIntent = intent
    }
}