package com.izubot.treinemais.ui.training_log

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun TrainingLog(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    trainingLogViewModel: TrainingLogViewModel = hiltViewModel<TrainingLogViewModel>()
) {

    val uiState = trainingLogViewModel.uiState.collectAsState()

    Column(modifier.fillMaxSize()) {
        Text("Olá")
        Text(uiState.value.teste.toString())
    }
}