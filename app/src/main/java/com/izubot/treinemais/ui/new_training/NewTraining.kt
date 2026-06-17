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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izubot.treinemais.ui.components.OutlinedTextFieldComponent
import com.izubot.treinemais.ui.theme.TreineMaisTheme
import kotlinx.coroutines.launch
import java.util.UUID

class ExerciseState(
    val id: String = UUID.randomUUID().toString(),
    initialName: String = "",
    initialSets: String = "0",
    initialReps: String = "0",
    initialWeight: String = "0"
) {
    var name by mutableStateOf(initialName)
    var sets by mutableStateOf(initialSets)
    var reps by mutableStateOf(initialReps)
    var weight by mutableStateOf(initialWeight)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTraining(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val exercises = remember { 
        mutableStateListOf<ExerciseState>().apply {
            add(ExerciseState(initialName = "Agachamento", initialSets = "3", initialReps = "12", initialWeight = "60"))
        }
    }
    var trainingName by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Voltar")
                        }
                    },
                    title = {
                        Text(
                            text = "Novo Treino",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    actions = {
                        Text(
                            text = "Salvar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { /* Salvar treino */ }
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
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextFieldComponent(
                    value = trainingName,
                    onValueChange = { trainingName = it },
                    textStyle = MaterialTheme.typography.titleMedium,
                    labelText = "Nome do treino",
                    shape = 8.dp,
                    leadingIcon = Icons.Default.BorderColor,
                    placeholderText = "Ex: Full Body Explosivo",
                    color = defaultTextFieldColors,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                )
            }

            item {
                AddExerciseHeader(onAddClick = { exercises.add(ExerciseState()) })
            }

            items(exercises, key = { it.id }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    modifier = Modifier.animateItem(),
                    onRemove = {
                        val index = exercises.indexOf(exercise)
                        exercises.remove(exercise)
                        
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "${exercise.name.ifEmpty { "Exercício" }} removido",
                                actionLabel = "Desfazer",
                                duration = androidx.compose.material3.SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                exercises.add(index, exercise)
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
                text = "Exercícios",
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
                    contentDescription = "Adicionar Exercício",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = "Defina séries, repetições e peso",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun ExerciseCard(
    exercise: ExerciseState,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                BasicTextField(
                    value = exercise.name,
                    onValueChange = { exercise.name = it },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
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
                                text = "Nome do exercício",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    }
                )

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remover",
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
                    label = "Séries",
                    value = exercise.sets,
                    onValueChange = { exercise.sets = it },
                    modifier = Modifier.weight(1f)
                )
                ExerciseDetailItem(
                    label = "Repetições",
                    value = exercise.reps,
                    onValueChange = { exercise.reps = it },
                    modifier = Modifier.weight(1.2f)
                )
                ExerciseDetailItem(
                    label = "Peso (kg)",
                    value = exercise.weight,
                    onValueChange = { exercise.weight = it },
                    modifier = Modifier.weight(1.2f)
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
    modifier: Modifier = Modifier
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
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
                onDismiss = {}
            )
        }
    }
}