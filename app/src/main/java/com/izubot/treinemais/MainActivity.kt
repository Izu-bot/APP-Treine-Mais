package com.izubot.treinemais

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.izubot.treinemais.ui.login.Login
import com.izubot.treinemais.ui.navigation.AppNavigation
import com.izubot.treinemais.ui.register.Register
import com.izubot.treinemais.ui.splash.Splash
import com.izubot.treinemais.ui.theme.TreineMaisTheme
import com.izubot.treinemais.ui.welcome.Welcome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TreineMaisTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}