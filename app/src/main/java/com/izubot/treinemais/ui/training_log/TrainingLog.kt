package com.izubot.treinemais.ui.training_log

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.ui.components.EditableExerciseTextField
import com.izubot.treinemais.ui.theme.TreineMaisTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingLog(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    trainingLogViewModel: TrainingLogViewModel = hiltViewModel<TrainingLogViewModel>()
) {
    val uiState by trainingLogViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    val totalExercise = uiState.training?.exercises?.size ?: 0
    val completedExercise = uiState.confirmedExerciseIds.size

    val progress = if (totalExercise > 0) {
        completedExercise.toFloat() / totalExercise
    } else 0F

    val pagerState = rememberPagerState(pageCount = {
        uiState.training?.exercises?.size ?: 0
    })

    if (uiState.openDialog) {
        AlertDialog(
            onDismissRequest = { trainingLogViewModel.handlerDialog() },
            confirmButton = {
                TextButton(onClick = {
                    trainingLogViewModel.handlerDialog()
                    onNavigateBack()
                }) {
                    Text(stringResource(R.string.home_abandon_training_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { trainingLogViewModel.handlerDialog() }) {
                    Text(stringResource(R.string.home_abandon_training_dismiss))
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.home_abandon_training_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.home_abandon_training_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.training?.title ?: "")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { trainingLogViewModel.handlerDialog() }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            uiState.training?.let { training ->

                CountExercises(
                    totalExercise,
                    completedExercise,
                    progress
                )

                Spacer(modifier = Modifier.height(2.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) { pageIndex ->
                    val currentExercise = training.exercises[pageIndex]
                    Column(modifier = modifier.padding(16.dp)) {
                        RecordValues(
                            currentExercise = currentExercise,
                            uiState = uiState,
                            onAddSet = { exerciseId -> trainingLogViewModel.addSet(exerciseId) },
                            onRemoveSet = { exerciseId -> trainingLogViewModel.removeSet(exerciseId) },
                            onUpdateSet = { exerciseId, index, reps, weight ->
                                trainingLogViewModel.updateSetLog(exerciseId, index, reps, weight)
                            },
                            onToggleCompleted = { exerciseId, index ->
                                trainingLogViewModel.toggleExerciseConfirmation(exerciseId, index)
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val scope = rememberCoroutineScope()
                val isLastPage = pagerState.currentPage == totalExercise - 1

                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage - 1,
                                animationSpec = spring(stiffness = Spring.StiffnessLow)
                            )
                        }
                    },
                    enabled = pagerState.currentPage > 0
                ) {
                    Text(
                        text = stringResource(R.string.training_log_back),
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        if (isLastPage) {
                            trainingLogViewModel.finishTraining(onNavigateBack)
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    page = pagerState.currentPage + 1,
                                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                                )
                            }
                        }
                    },
                    enabled = !isLastPage || progress == 1f
                ) {
                    Text(
                        text = if (isLastPage) stringResource(R.string.training_log_finish) else stringResource(R.string.training_log_next),
                        fontWeight = FontWeight.Bold,
                        color = if (isLastPage && progress == 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        SuccessOverlay(
            isSaving = uiState.isSaving,
            showTrophy = uiState.showTrophy
        )
    }
}

@Composable
fun SuccessOverlay(
    isSaving: Boolean,
    showTrophy: Boolean
) {
    AnimatedVisibility(
        visible = isSaving || showTrophy,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.training_log_saving_progress),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                AnimatedVisibility(
                    visible = showTrophy,
                    enter = scaleIn(initialScale = 0.5f) + fadeIn(),
                    exit = scaleOut(targetScale = 0.5f) + fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Troféu",
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.training_log_completed_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.training_log_completed_subtitle),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecordValues(
    currentExercise: Exercise,
    uiState: TrainingLogUiState,
    onAddSet: (String) -> Unit,
    onRemoveSet: (String) -> Unit,
    onUpdateSet: (String, Int, String?, String?) -> Unit,
    onToggleCompleted: (String, Int) -> Unit
) {
    val weightSet = 0.2f
    val weightReps = 0.3f
    val weightKg = 0.4f
    val sets = uiState.exerciseSet[currentExercise.id] ?: emptyList()

    Column {
        Text(
            text = currentExercise.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "",
                Modifier.size(12.dp)
            )
            Text(
                text = "${currentExercise.sets}x${currentExercise.reps}",
                style = MaterialTheme.typography.labelMedium
            )
        }
        HorizontalDivider(thickness = 1.dp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            itemsIndexed(sets) { index, setLog ->
                EditableSetRow(
                    exerciseId = currentExercise.id,
                    setIndex = index,
                    setLog = setLog,
                    weights = Triple(weightSet, weightReps, weightKg),
                    onUpdateSet = onUpdateSet,
                    onToggleCompleted = onToggleCompleted
                )
            }
            item {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onAddSet(currentExercise.id) },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.training_log_add_set))
                    }

                    OutlinedButton(
                        onClick = { onRemoveSet(currentExercise.id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.training_log_remove_set))
                    }
                }
            }
        }
    }
}

@Composable
fun EditableSetRow(
    exerciseId: String,
    setIndex: Int,
    setLog: ExerciseSetLog,
    weights: Triple<Float, Float, Float>,
    onUpdateSet: (String, Int, String?, String?) -> Unit,
    onToggleCompleted: (String, Int) -> Unit
) {
    val isDone = setLog.isCompleted
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditableExerciseTextField(
            value = (setIndex + 1).toString(),
            onValueChange = { },
            readOnly = true,
            enabled = !isDone,
            modifier = Modifier.weight(weights.first),
            labelText = stringResource(R.string.training_log_set_label, setIndex + 1)
        )

        EditableExerciseTextField(
            value = setLog.reps,
            onValueChange = { onUpdateSet(exerciseId, setIndex, it, null) },
            readOnly = isDone,
            enabled = !isDone,
            modifier = Modifier.weight(weights.second),
            labelText = stringResource(R.string.training_log_reps_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )

        EditableExerciseTextField(
            value = setLog.weight,
            onValueChange = { onUpdateSet(exerciseId, setIndex, null, it) },
            readOnly = isDone,
            enabled = !isDone,
            modifier = Modifier.weight(weights.third),
            labelText = stringResource(R.string.training_log_kg_label),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )

        FilledIconButton(
            onClick = { onToggleCompleted(exerciseId, setIndex) },
            shape = CircleShape,
            modifier = Modifier.size(50.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isDone) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (isDone) Icons.Default.Edit else Icons.Default.Check,
                contentDescription = if (isDone) "Editar" else "Confirmar"
            )
        }
    }
}

@Composable
fun CountExercises(
    totalExercise: Int,
    completedExercise: Int,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.training_log_progress_title),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(
                        R.string.training_log_progress_count,
                        completedExercise,
                        totalExercise
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AbandonTrainingDialogPreview() {
    TreineMaisTheme(dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Deseja abandonar o treino?",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Seu progresso não será salvo se você sair agora.",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {}) { Text("Cancelar") }

                    Spacer(Modifier.width(8.dp))

                    TextButton(onClick = {}) { Text("Confirmar") }
                }
            }
        }
    }
}
