package com.izubot.treinemais.ui.new_training

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.OutlinedTextFieldComponent
import com.izubot.treinemais.ui.theme.TreineMaisTheme
import com.izubot.treinemais.ui.utils.clearFocusOnTap
import com.izubot.treinemais.utils.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTraining(
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    newTrainingViewModel: NewTrainingViewModel = hiltViewModel<NewTrainingViewModel>()
) {
    val state by newTrainingViewModel.state.collectAsState()

    val scope = rememberCoroutineScope()

    val exerciseRemovedMsg = stringResource(R.string.new_training_exercise_removed)
    val undoMsg = stringResource(R.string.new_training_undo)

    val defaultTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
    )

    LaunchedEffect(key1 = Unit) {
        newTrainingViewModel.channel.collect { event ->
            when (event) {
                is UiEvent.Success -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Error -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Info -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(R.string.new_training_back_desc))
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.new_training_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    actions = {
                        Text(
                            text = stringResource(R.string.new_training_save),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isSaving) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(enabled = !state.isSaving) {
                                newTrainingViewModel.saveTraining(onSuccess = onDismiss)
                            }
                        )
                    }
                )
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .clearFocusOnTap()
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextFieldComponent(
                    value = state.trainingName,
                    onValueChange = { newTrainingViewModel.onTrainingNameChange(it) },
                    textStyle = MaterialTheme.typography.titleMedium,
                    labelText = stringResource(R.string.new_training_name_label),
                    shape = 8.dp,
                    leadingIcon = Icons.Default.BorderColor,
                    placeholderText = stringResource(R.string.new_training_name_placeholder),
                    color = defaultTextFieldColors,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    isError = state.trainingNameError,
                    errorMessage = state.message
                )
            }

            item {
                AddExerciseHeader(onAddClick = { newTrainingViewModel.addExercise() })
            }

            items(state.exercises, key = { it.id }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    modifier = Modifier.animateItem(),
                    onChangeName = { newName ->
                        newTrainingViewModel.updateExercise(exercise.id) { it.copy(name = newName) }
                    },
                    onChangeSets = { newSets ->
                        newTrainingViewModel.updateExercise(exercise.id) { it.copy(sets = newSets) }
                    },
                    onChangeReps = { newReps ->
                        newTrainingViewModel.updateExercise(exercise.id) { it.copy(reps = newReps) }
                    },
                    onChangeWeight = { newWeight ->
                        newTrainingViewModel.updateExercise(exercise.id) { it.copy(weight = newWeight) }
                    },
                    onRemove = {
                        val currentIndex = state.exercises.indexOf(exercise)

                        newTrainingViewModel.removeExercise(exercise)

                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "${exercise.name} $exerciseRemovedMsg",
                                actionLabel = undoMsg,
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                newTrainingViewModel.undoRemove(currentIndex, exercise)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AddExerciseHeader(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.new_training_exercises_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(width = 60.dp, height = 40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = stringResource(R.string.new_training_add_exercise_desc),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = stringResource(R.string.new_training_exercises_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun ExerciseCard(
    exercise: ExerciseUiState,
    onChangeName: (String) -> Unit,
    onChangeSets: (String) -> Unit,
    onChangeReps: (String) -> Unit,
    onChangeWeight: (String) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (exercise.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val iconColor = if (exercise.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val borderColor = if (exercise.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant

    Surface(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )

                BasicTextField(
                    value = exercise.name,
                    onValueChange = { if (it.length <= 100) onChangeName(it) },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = contentColor,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (exercise.name.isEmpty()) {
                            Text(
                                text = stringResource(R.string.new_training_exercise_name_placeholder),
                                style = MaterialTheme.typography.titleMedium,
                                color = contentColor.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    })

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.new_training_remove_exercise_desc),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseDetailItem(
                    label = stringResource(R.string.new_training_sets),
                    value = exercise.sets,
                    onValueChange = onChangeSets,
                    modifier = Modifier.weight(1f),
                    imeAction = ImeAction.Next
                )
                ExerciseDetailItem(
                    label = stringResource(R.string.new_training_reps),
                    value = exercise.reps,
                    onValueChange = onChangeReps,
                    modifier = Modifier.weight(1.2f),
                    imeAction = ImeAction.Next
                )
                ExerciseDetailItem(
                    label = stringResource(R.string.new_training_weight),
                    value = exercise.weight,
                    onValueChange = onChangeWeight,
                    modifier = Modifier.weight(1.2f),
                    imeAction = ImeAction.Done
                )
            }
        }
    }
}

@Composable
fun ExerciseDetailItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.width(32.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun NewTrainingDarkPreview() {
    TreineMaisTheme(darkTheme = true, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NewTraining(
                snackbarHostState = remember { SnackbarHostState() },
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun NewTrainingLightPreview() {
    TreineMaisTheme(darkTheme = false, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NewTraining(
                snackbarHostState = remember { SnackbarHostState() },
                onDismiss = {}
            )
        }
    }
}