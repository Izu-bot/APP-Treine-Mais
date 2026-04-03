package com.izubot.treinemais.data.local.helpers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _sessionExpired = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val sessionExpired = _sessionExpired.asSharedFlow()

    fun triggerSessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }
}