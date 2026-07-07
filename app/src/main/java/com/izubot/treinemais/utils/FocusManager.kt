package com.izubot.treinemais.utils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusManager @Inject constructor() {
    private val _focusActions = Channel<FocusAction>(Channel.BUFFERED)
    val focusActions = _focusActions.receiveAsFlow()

    fun clearFocus() {
        _focusActions.trySend(FocusAction.Clear)
    }
}

sealed class FocusAction {
    data object Clear : FocusAction()
}
