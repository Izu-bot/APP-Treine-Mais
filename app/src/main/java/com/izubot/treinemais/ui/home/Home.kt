package com.izubot.treinemais.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.DayProgress
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.utils.FocusAction
import java.time.format.TextStyle

@Composable
fun Home(
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val state by homeViewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        homeViewModel.focusActions.collect { action ->
            when (action) {
                is FocusAction.Clear -> focusManager.clearFocus()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WeeklyTrackerCard(state.weeklyProgress)

        Spacer(modifier = Modifier.height(24.dp))

        if (state.trainings == null) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AnimatedContent(
                targetState = state.selectedTraining,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "TrainingTransition",
                modifier = Modifier.weight(1f, fill = false)
            ) { selectedTraining ->
                if (selectedTraining == null) {
                    QuickTrainingSelection(
                        trainings = state.trainings!!,
                        onTrainingSelected = {
                            focusManager.clearFocus()
                            homeViewModel.selectTraining(it)
                        }
                    )
                } else {
                    ActiveTrainingLogger(
                        training = selectedTraining,
                        exerciseWeights = state.exerciseWeights,
                        onWeightChange = { exerciseId, setIndex, weight ->
                            homeViewModel.onWeightChange(exerciseId, setIndex, weight)
                        },
                        onClose = {
                            focusManager.clearFocus()
                            homeViewModel.resetTrainingSelection()
                        },
                        onComplete = {
                            focusManager.clearFocus()
                            homeViewModel.completeTraining()
                        },
                        onClearFocus = { focusManager.clearFocus() },
                        state = state,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun QuickTrainingSelection(
    trainings: List<Training>,
    onTrainingSelected: (Training) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.home_choose_training),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (trainings.isEmpty()) {
            Text(
                text = stringResource(R.string.home_no_trainings_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(trainings) { training ->
                    Card(
                        modifier = Modifier
                            .size(width = 160.dp, height = 100.dp)
                            .clickable { onTrainingSelected(training) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = training.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveTrainingLogger(
    state: HomeUiState,
    homeViewModel: HomeViewModel,
    training: Training,
    exerciseWeights: Map<String, List<String>>,
    onWeightChange: (String, Int, String) -> Unit,
    onClose: () -> Unit,
    onComplete: () -> Unit,
    onClearFocus: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }

    val allFieldsFilled by remember(training, exerciseWeights) {
        derivedStateOf {
            training.exercises.all { exercise ->
                val weights = exerciseWeights[exercise.id]
                weights != null && weights.all { it.isNotBlank() }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.home_abandon_training_title)) },
            text = { Text(stringResource(R.string.home_abandon_training_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onClose()
                }) {
                    Text(stringResource(R.string.home_abandon_training_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.home_abandon_training_dismiss))
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = training.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showExitDialog = true }) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    items = training.exercises,
                    key = { _, exercise -> exercise.id },
                    contentType = { _, _ -> "exercise" }
                ) { index, exercise ->
                    val previousExercisesCompleted by remember(
                        training.exercises,
                        exerciseWeights,
                        index
                    ) {
                        derivedStateOf {
                            training.exercises.take(index).all { prev ->
                                val weights = exerciseWeights[prev.id]
                                weights != null && weights.all { it.isNotBlank() }
                            }
                        }
                    }

                    ExerciseLogItem(
                        exercise = exercise,
                        weights = exerciseWeights[exercise.id] ?: emptyList(),
                        isEnabled = previousExercisesCompleted,
                        isConfirmed = state.confirmedExerciseIds.contains(exercise.id),
                        onWeightChange = { setIndex, weight ->
                            onWeightChange(exercise.id, setIndex, weight)
                        },
                        onConfirm = { homeViewModel.confirmExercise(exercise.id) },
                        onFastFill = { weight ->
                            val numSets = exercise.sets?.toIntOrNull() ?: 0
                            repeat(numSets) { i ->
                                onWeightChange(exercise.id, i, weight)
                            }

                            homeViewModel.confirmExercise(exercise.id)
                        },
                        onDone = { onClearFocus() },
                        onUnlock = { homeViewModel.unlockExercise(exercise.id) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onComplete,
                modifier = Modifier.fillMaxWidth(),
                enabled = allFieldsFilled
            ) {
                Text(stringResource(R.string.home_complete_training))
            }
        }
    }
}

@Composable
fun ExerciseLogItem(
    exercise: Exercise,
    weights: List<String>,
    isEnabled: Boolean,
    isConfirmed: Boolean,
    onWeightChange: (Int, String) -> Unit,
    onConfirm: () -> Unit,
    onFastFill: (String) -> Unit,
    onDone: () -> Unit,
    onUnlock: () -> Unit
) {
    val scrollState = rememberScrollState()
    val defaultWeight = exercise.weight ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEnabled) 1f else 0.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            if (isEnabled && !isConfirmed && defaultWeight.isNotBlank()) {
                AssistChip(
                    onClick = { onFastFill(defaultWeight) },
                    label = {
                        Text(
                            stringResource(
                                R.string.home_apply_all,
                                defaultWeight
                            )
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }

            if (isConfirmed) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weights.forEachIndexed { index, weight ->
                val fieldWidth = if (weights.size <= 4) {
                    Modifier.weight(1f)
                } else {
                    Modifier.width(80.dp)
                }

                if (isConfirmed) {
                    Box(
                        modifier = fieldWidth
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onUnlock() }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Text(
                                text = "S${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${weight}kg",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { onWeightChange(index, it) },
                        modifier = fieldWidth.height(IntrinsicSize.Min),
                        label = {
                            Text(
                                "S${index + 1}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        suffix = { Text("kg", style = MaterialTheme.typography.labelSmall) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onConfirm()
                                onDone()
                            }
                        ),
                        singleLine = true,
                        enabled = isEnabled,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyTrackerCard(weeklyProgress: List<DayProgress>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.home_weekly_progress),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyProgress.forEach { day ->
                    DayItem(day)
                }
            }
        }
    }
}

@Composable
fun DayItem(day: DayProgress) {
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    val dayLetter = day.date.dayOfWeek.getDisplayName(TextStyle.NARROW, locale)
    val isCompleted = day.isCompleted
    val isToday = day.isToday

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = dayLetter,
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> MaterialTheme.colorScheme.primary
                        else -> Color.Transparent
                    }
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isCompleted -> Color.Transparent
                        isToday -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outlineVariant
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else if (isToday) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}
