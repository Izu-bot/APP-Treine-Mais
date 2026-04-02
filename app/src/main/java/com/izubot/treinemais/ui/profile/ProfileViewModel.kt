package com.izubot.treinemais.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.data.local.helpers.NotificationHelper
import com.izubot.treinemais.data.repository.UserRepository
import com.izubot.treinemais.domain.usecase.GetAiUseCase
import com.izubot.treinemais.domain.usecase.GetNotificationUseCase
import com.izubot.treinemais.domain.usecase.GetProfileImageUriUseCase
import com.izubot.treinemais.domain.usecase.GetThemeUseCase
import com.izubot.treinemais.domain.usecase.SaveAiUseCase
import com.izubot.treinemais.domain.usecase.SaveNotificationUseCase
import com.izubot.treinemais.domain.usecase.SaveProfileImageUriUseCase
import com.izubot.treinemais.domain.usecase.SaveThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    getNotificationUseCase: GetNotificationUseCase,
    private val saveNotificationUseCase: SaveNotificationUseCase,
    getImageUriUseCase: GetProfileImageUriUseCase,
    private val saveImageUriUseCase: SaveProfileImageUriUseCase,
    getAiUseCase: GetAiUseCase,
    private val saveAiUseCase: SaveAiUseCase,
    private val notificationHelper: NotificationHelper,
    userRepository: UserRepository
) : ViewModel() {
    private val _channel = Channel<UiEvent>()
    val channel = _channel.receiveAsFlow()

    sealed class UiEvent {
        data class Toast(val message: String) : UiEvent()
    }

    private val _dbUserFlow = userRepository.getUserById()

    private val _localState = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = combine(
        _localState,
        getThemeUseCase.themeCache,
        getNotificationUseCase.notificationCache,
        getImageUriUseCase.imageUrlCache,
        getAiUseCase.aiCache,
    ) { local, theme, notification, imageUri, aiEnabled ->
        local.copy(
            isLoading = false,
            themeCheck = theme,
            notificationCheck = notification,
            isAiEnabled = aiEnabled,
            imageUri = when {
                imageUri.isEmpty() -> ""
                imageUri.startsWith("file://") || imageUri.startsWith("http") -> imageUri
                else -> "file://$imageUri"
            },
        )
    }.combine( _dbUserFlow ) { stateSoFar, dbUser ->
        stateSoFar.copy(
            name = dbUser?.fullName ?: stateSoFar.name,
            email = dbUser?.email ?: stateSoFar.email,
            gender = dbUser?.gender ?: stateSoFar.gender,
            birthDate = dbUser?.birthDate?.let { LocalDate.parse(it) } ?: stateSoFar.birthDate,
            goals = dbUser?.gender ?: stateSoFar.goals,
            imageUri = dbUser?.localPhotoPath ?: stateSoFar.imageUri
        )
    }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = _localState.value
    )

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val localPath = saveImageToInternalStorage(uri)
            localPath?.let {
                saveImageUriUseCase(it)
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            
            context.filesDir.listFiles { _, name ->
                name.startsWith("profile_image_") 
            }?.forEach { it.delete() }

            val fileName = "profile_image_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun onChangeName(name: String) {
        _localState.update { it.copy(name = name) }
    }

    fun onChangeEmail(email: String) {
        _localState.update { it.copy(email = email) }
    }

    fun onDismissDatePicker() {
        _localState.update { it.copy(showDatePicker = false) }
    }

    fun onShowDatePicker() {
        _localState.update { it.copy(showDatePicker = true) }
    }

    fun onDateSelected(date: LocalDate) {
        _localState.update { it.copy(birthDate = date) }
    }

    fun onSwitchTheme() {
        val newThemeValue = !state.value.themeCheck
        viewModelScope.launch {
            saveThemeUseCase(newThemeValue)
        }
    }

    fun onSwitchNotification() {
        val nextValue = !state.value.notificationCheck
        viewModelScope.launch {
            saveNotificationUseCase(nextValue)
            if (nextValue) {
                notificationHelper.showSimpleNotification(
                    title = context.getString(R.string.notification_title),
                    message = context.getString(R.string.notification_message),
                )
            } else {
                notificationHelper.cancelAllNotifications()
            }
        }
    }

    fun onSwitchAiMode() {
        val nextValue = !state.value.isAiEnabled
        viewModelScope.launch {
            saveAiUseCase(nextValue)
            _channel.send(UiEvent.Toast(if (nextValue) context.getString(R.string.ai_title_activated) else context.getString(R.string.ai_title_deactivated)))
        }
    }
}
