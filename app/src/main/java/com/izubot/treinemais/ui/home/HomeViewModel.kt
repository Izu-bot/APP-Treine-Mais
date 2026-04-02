package com.izubot.treinemais.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getUserNameUseCase: GetUserUseCase
) : ViewModel() {

    private val _localState = MutableStateFlow(HomeUiState())
    val state = _localState

    init {
        greet()
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            getUserNameUseCase().collect { user ->
                _localState.update {
                    it.copy(
                        nameUser = user?.fullName ?: "",
                        imageUri = user?.localPhotoPath ?: ""
                    )
                }
            }
        }
    }

    fun greet() {
        val currentHour = LocalTime.now()

        val resourceId =  when (currentHour.hour) {
            in 6..11 -> context.getString(R.string.welcome_good_morning)
            in 12..17 -> context.getString(R.string.welcome_good_afternoon)
            else -> context.getString(R.string.welcome_good_night)
        }

        _localState.update {
            it.copy(greeting = resourceId)
        }
    }
}
