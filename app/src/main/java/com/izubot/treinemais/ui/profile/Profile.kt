package com.izubot.treinemais.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.PerfilComponent
import com.izubot.treinemais.utils.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun Profile(
    onDismiss: () -> Unit = {},
    profileViewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>()
) {
    val state by profileViewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val focusManage = LocalFocusManager.current
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    val lifecycleOwner = LocalLifecycleOwner.current

    var isPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Verificação implicita, Android < 13
            }
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val currentPermissionStatus = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    isPermissionGranted = currentPermissionStatus

                    if (currentPermissionStatus != state.notificationCheck) {
                        profileViewModel.onSwitchNotification()
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.channel.collectLatest { evet ->
            when (evet) {
                is UiEvent.Toast -> {
                    Toast.makeText(context, evet.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                profileViewModel.onImageSelected(it)
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            isPermissionGranted = isGranted
            if (isGranted) {
                profileViewModel.onSwitchNotification()
            }
        }
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
                imageUrl = state.imageUri,
                size = 120.dp,
                onPerfilClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                editable = true,
                userName = state.name
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
                    onSwitchAiMode = { profileViewModel.onSwitchAiMode() },
                    isPermissionGranted = isPermissionGranted,
                    context = context,
                    permissionLauncher = permissionLauncher
                )

                LogoutComponent(
                    state = state,
                    onShowDialog = { profileViewModel.onShowDialog() },
                    onDismiss = { profileViewModel.onDismissDialog() },
                    onLogout = { profileViewModel.onLogout() }
                )
            }
        }
    }
}
