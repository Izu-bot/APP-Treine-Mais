package com.izubot.treinemais.ui.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.BuildConfig
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.AppInformation
import com.izubot.treinemais.ui.components.ButtonComponent
import com.izubot.treinemais.ui.components.OutlinedTextFieldComponent
import com.izubot.treinemais.ui.components.PerfilComponent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun Profile(
    onDismiss: () -> Unit = {},
    profileViewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>()
) {
    val state by profileViewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val focusManage = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_profile),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    focusManage.clearFocus()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            PerfilComponent(
                imageUrl = selectedImageUri?.toString() ?: "",
                size = 120.dp,
                onPerfilClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                editable = true
            )

            Spacer(modifier = Modifier.height(56.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                PersonalInfo(
                    state = state,
                    onChangeName = { profileViewModel.onChangeName(it) },
                    onDismissDatePicker = { profileViewModel.onDismissDatePicker() },
                    onDateSelected = { profileViewModel.onDateSelected(it) },
                    onShowDatePicker = { profileViewModel.onShowDatePicker() },
                    onChangeEmail = { profileViewModel.onChangeEmail(it) },
                )

                AppSettings(
                    uiState = state,
                    onSwitchTheme = { profileViewModel.onSwitchTheme() },
                    onSwitchNotification = { profileViewModel.onSwitchNotification() },
                    onSwitchAiMode = { profileViewModel.onSwitchAiMode() }
                )
            }
        }
    }
}

@Composable
fun PersonalInfo(
    state: ProfileUiState,
    onChangeName: (String) -> Unit = {},
    onChangeEmail: (String) -> Unit = {},
    onDismissDatePicker: () -> Unit = {},
    onDateSelected: (LocalDate) -> Unit = {},
    onShowDatePicker: () -> Unit = {},
) {
    val datePickerState = rememberDatePickerState()

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismissDatePicker,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(selectedDate)
                        }
                        onDismissDatePicker()
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismissDatePicker) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.personal_information),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.inverseSurface
        )

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextFieldComponent(
            value = state.name,
            onValueChange = onChangeName,
            isPasswordField = false,
            labelText = stringResource(R.string.profile_name),
            leadingIcon = Icons.Rounded.Person,
            placeholderText = "",
            shape = 8.dp,
            textStyle = MaterialTheme.typography.bodyLarge,
            color = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )

        OutlinedTextFieldComponent(
            value = state.email,
            onValueChange = onChangeEmail,
            isPasswordField = false,
            labelText = stringResource(R.string.profile_email),
            leadingIcon = Icons.Rounded.Person,
            placeholderText = "",
            shape = 8.dp,
            textStyle = MaterialTheme.typography.bodyLarge,
            color = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )

        OutlinedTextFieldComponent(
            value = if (state.gender == null) "Não informado" else state.gender.toString(),
            onValueChange = {},
            isPasswordField = false,
            labelText = stringResource(R.string.profile_gender),
            leadingIcon = Icons.Rounded.Person,
            placeholderText = "",
            readOnly = true,
            shape = 8.dp,
            textStyle = MaterialTheme.typography.bodyLarge,
            color = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextFieldComponent(
                value = if (state.birthDate == null) "Não informado"
                    else state.birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())),
                onValueChange = {},
                readOnly = true,
                isPasswordField = false,
                labelText = stringResource(R.string.profile_birthdate),
                leadingIcon = Icons.Rounded.Person,
                placeholderText = "",
                shape = 8.dp,
                textStyle = MaterialTheme.typography.bodyLarge,
                color = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0f)
                    .clickable { onShowDatePicker() }
            )
        }
    }
}

@Composable
fun AppSettings(
    onSwitchTheme: () -> Unit = {},
    onSwitchNotification: () -> Unit = {},
    uiState: ProfileUiState = ProfileUiState(),
    onSwitchAiMode: () -> Unit = {}
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
                    checked = uiState.notificationCheck,
                    onCheckedChange = { onSwitchNotification()
                        Log.d("Notificação", "${uiState.notificationCheck}") }
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