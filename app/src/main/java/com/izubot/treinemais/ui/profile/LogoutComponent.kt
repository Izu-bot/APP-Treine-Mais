package com.izubot.treinemais.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.izubot.treinemais.R

@Composable
@Preview
fun LogoutComponent(
    state: ProfileUiState = ProfileUiState(),
    onShowDialog: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.profile_logout),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.clickable { onShowDialog() }
        )

        if (state.showDialog) {
            AlertDialog(
                icon = {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                },
                title = {
                    Text(
                        text = stringResource(R.string.profile_logout_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.profile_logout_message),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                },
                onDismissRequest = {
                    onDismiss()
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onLogout()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.profile_logout_confirm),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.profile_logout_cancel),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            )
        }
    }
}

