package com.izubot.treinemais.utils

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FocusManagerViewModel @Inject constructor(
    val focusManager: FocusManager
) : ViewModel()
