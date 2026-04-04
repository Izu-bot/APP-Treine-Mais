package com.izubot.treinemais.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.izubot.treinemais.BuildConfig
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.AppInformation
import androidx.core.content.edit

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppSettings(
    permissionLauncher: ActivityResultLauncher<String>,
    onSwitchTheme: () -> Unit = {},
    onSwitchNotification: () -> Unit = {},
    uiState: ProfileUiState = ProfileUiState(),
    onSwitchAiMode: () -> Unit = {},
    isPermissionGranted: Boolean = false,
    context: Context = LocalContext.current
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.profile_application_information),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.inverseSurface
        )

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)

        Spacer(modifier = Modifier.height(24.dp))

        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.profile_settings_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.profile_support_theme),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            },
            trailingContent = {
                Switch(
                    checked = uiState.themeCheck,
                    onCheckedChange = { onSwitchTheme()
                        Log.d("Tema", "${uiState.themeCheck}")}
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.profile_notification),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.profile_support_notification),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            },
            trailingContent = {
                Switch(
                    checked = uiState.notificationCheck && isPermissionGranted,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            val activity = context as? Activity

                            when {
                                // Já tem permissão
                                isPermissionGranted -> onSwitchNotification()

                                // Android deve mostrar rationale (usuário negou, mas não bloqueou)
                                activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                                    activity,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) -> {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }

                                // Primeira vez OU Negado permanentemente
                                else -> {
                                    val preferences = context.getSharedPreferences("app_terms", Context.MODE_PRIVATE)
                                    val hasAskedBefore = preferences.getBoolean("notification_asked", false)

                                    if (hasAskedBefore) {
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        val uri = "package:${context.packageName}".toUri()
                                        intent.data = uri
                                        context.startActivity(intent)
                                    } else {
                                        preferences.edit { putBoolean("notification_asked", true) }
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, R.string.profile_notification_disabled , Toast.LENGTH_SHORT).show()
                            onSwitchNotification()
                        }
                    }
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.profile_intelligence_artificial),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(R.string.profile_support_ai),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            },
            trailingContent = {
                Switch(
                    checked = uiState.isAiEnabled,
                    onCheckedChange = { onSwitchAiMode()
                        Log.d("Inteligência Artificial", "${uiState.isAiEnabled}") }
                )
            }
        )

        AppInformation(
            label = stringResource(R.string.profile_version),
            trailingText = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
            icon = Icons.Rounded.Info,
            onClick = {}
        )

        AppInformation(
            label = stringResource(R.string.profile_privacy_policy),
            icon = Icons.Rounded.Info,
            onClick = {}
        )

        AppInformation(
            label = stringResource(R.string.profile_terms_of_use),
            icon = Icons.Rounded.Info,
            onClick = {}
        )
    }
}
