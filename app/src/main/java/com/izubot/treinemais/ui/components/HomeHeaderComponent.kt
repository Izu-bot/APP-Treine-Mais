package com.izubot.treinemais.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HomeHeaderComponent(
    isNotificationActive: Boolean = false,
    onNavigateToPerfil: () -> Unit = {},
    onNavigateToNotified: () -> Unit = {}
) {
    TopAppBar(
        modifier = Modifier.height(60.dp),
        title = {},
        navigationIcon = {
            PerfilComponent(
                modifier = Modifier.padding(start = 8.dp),
                onPerfilClick = { onNavigateToPerfil }
            )
        },
        actions = {
            IconButton(
                onClick = { onNavigateToNotified }
            ) {
                if (isNotificationActive) {
                    Image(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificações",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
                    )
                } else {
                    Image(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notificações",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
                    )
                }
            }
        },
    )
}