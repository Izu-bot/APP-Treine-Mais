package com.izubot.treinemais.ui.edit_exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.OutlinedTextFieldComponent
import com.izubot.treinemais.ui.utils.clearFocusOnTap
import com.izubot.treinemais.utils.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExercise(
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditExerciseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isEditing = state.id.isNotEmpty()

    LaunchedEffect(key1 = Unit) {
        viewModel.channel.collect { event ->
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
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (isEditing) stringResource(R.string.new_training_exercises_title) 
                        else "Adicionar Exercício"
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    Text(
                        text = stringResource(R.string.new_training_save),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { viewModel.updateExercise(onDismiss) },
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .clearFocusOnTap(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                OutlinedTextFieldComponent(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    labelText = stringResource(R.string.new_training_exercise_name_placeholder),
                    placeholderText = "",
                    modifier = Modifier.fillMaxWidth(),
                    shape = 12.dp,
                    color = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EditDetailItem(
                        label = stringResource(R.string.new_training_sets),
                        value = state.sets,
                        onValueChange = viewModel::onSetsChange,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next
                    )
                    EditDetailItem(
                        label = stringResource(R.string.new_training_reps),
                        value = state.reps,
                        onValueChange = viewModel::onRepsChange,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next
                    )
                    EditDetailItem(
                        label = stringResource(R.string.new_training_weight),
                        value = state.weight,
                        onValueChange = viewModel::onWeightChange,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Done
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isEditing) {
                    Button(
                        onClick = { viewModel.deleteExercise(onDismiss) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.new_training_remove_exercise_desc), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun EditDetailItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                modifier = Modifier.width(40.dp)
            )
        }
    }
}
