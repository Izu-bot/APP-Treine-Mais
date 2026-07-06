package com.izubot.treinemais.ui.training

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.ui.components.CardMyTrainingsComponent
import com.izubot.treinemais.utils.UiEvent

@Composable
fun Training(
    snackbarHostState: SnackbarHostState,
    onNavigateToNewTraining: () -> Unit,
    onExerciseClick: (String, String) -> Unit,
    onAddExercise: (String) -> Unit,
    modifier: Modifier = Modifier,
    trainingViewModel: TrainingViewModel = hiltViewModel<TrainingViewModel>()
) {
    val state by trainingViewModel.state.collectAsState()

    var trainingToDelete by remember { mutableStateOf<Training?>(null) }

    LaunchedEffect(key1 = Unit) {
        trainingViewModel.channel.collect { event ->
            when (event) {
                is UiEvent.Success -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Error -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Info -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (trainingToDelete != null) {
        AlertDialog(
            onDismissRequest = { trainingToDelete = null },
            title = { Text(text = stringResource(R.string.training_delete_title)) },
            text = { Text(text = stringResource(R.string.training_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        trainingToDelete?.let { trainingViewModel.deleteTraining(it) }
                        trainingToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.training_delete_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { trainingToDelete = null }) {
                    Text(text = stringResource(R.string.training_delete_cancel))
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToNewTraining() }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(R.string.training_add_content_desc)
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.training_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.training_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                if (state.isListEmpty) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = state.messageTrainingsEmpty
                            ?: stringResource(R.string.training_empty_message),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 18.dp,
                            end = 18.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.trainings) { trainings ->
                            val isExpanded = state.expandedCardIds.contains(trainings.id)
                            CardMyTrainingsComponent(
                                training = trainings,
                                isExpanded = isExpanded,
                                onToggleCard = { trainingViewModel.onToggleCard(it) },
                                onDeleteTraining = { trainingToDelete = it },
                                onAddExercise = { onAddExercise(it) },
                                onExerciseClick = { exercise ->
                                    onExerciseClick(exercise.id, trainings.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
