package com.izubot.treinemais.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izubot.treinemais.data.local.datasource.DataStorePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStorePrefs: DataStorePrefs,
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        prepareAppData()
    }

    private fun prepareAppData() {
        viewModelScope.launch {
            dataStorePrefs.preload()
            _isReady.value = true
        }
    }
}