package com.izubot.treinemais.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusManager @Inject constructor() {
    private val _focusActions = MutableSharedFlow<FocusAction>(extraBufferCapacity = 1)
    val focusActions = _focusActions.asSharedFlow()

    fun clearFocus() {
        _focusActions.tryEmit(FocusAction.Clear)
    }
}

sealed class FocusAction {
    data object Clear : FocusAction()
}
