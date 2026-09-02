package com.izubot.treinemais.ui.progress

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.LoadEvolutionChart
import com.izubot.treinemais.ui.utils.clearFocusOnTap
import com.izubot.treinemais.utils.UiEvent
import java.util.Locale

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun Progress(
    snackbarHostState: SnackbarHostState,
    viewModel: ProgressViewModel = hiltViewModel<ProgressViewModel>()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetState()
        viewModel.channel.collect { event ->
            when (event) {
                is UiEvent.Success -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Error -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Info -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clearFocusOnTap()
        ) {
            Text(
                text = stringResource(R.string.progress_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cards de Resumo: Visão mensal para estatísticas gerais, baseada na sessão para treino/exercício específico.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = when (state.viewMode) {
                        ViewMode.GENERAL -> stringResource(R.string.progress_monthly_workouts)
                        ViewMode.TRAINING -> stringResource(R.string.progress_training_volume)
                        ViewMode.EXERCISE -> stringResource(R.string.progress_exercise_volume)
                    },
                    value = when (state.viewMode) {
                        ViewMode.GENERAL -> state.monthlyWorkouts.toString()
                        ViewMode.TRAINING -> String.format(Locale.US, "%.0f kg", state.lastVolume)
                        ViewMode.EXERCISE -> String.format(Locale.US, "%.1f kg", state.maxLoad)
                    },
                    subtitle = when (state.viewMode) {
                        ViewMode.GENERAL -> {
                            if (state.monthlyWorkoutsChange >= 0) {
                                stringResource(R.string.progress_monthly_workouts_change_positive, state.monthlyWorkoutsChange)
                            } else {
                                stringResource(R.string.progress_monthly_workouts_change_negative, state.monthlyWorkoutsChange)
                            }
                        }
                        ViewMode.TRAINING -> stringResource(R.string.progress_last_session)
                        ViewMode.EXERCISE -> stringResource(R.string.progress_estimated_1rm)
                    },
                    icon = when (state.viewMode) {
                        ViewMode.GENERAL -> Icons.Default.CalendarMonth
                        ViewMode.TRAINING -> Icons.Default.CalendarMonth
                        ViewMode.EXERCISE -> Icons.AutoMirrored.Filled.TrendingUp
                    },
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = when (state.viewMode) {
                        ViewMode.GENERAL -> stringResource(R.string.progress_monthly_volume)
                        ViewMode.TRAINING -> stringResource(R.string.progress_record_volume)
                        ViewMode.EXERCISE -> stringResource(R.string.progress_record_weight)
                    },
                    value = when (state.viewMode) {
                        ViewMode.GENERAL -> String.format(Locale.US, "%.0f kg", state.totalMonthlyVolume)
                        ViewMode.TRAINING -> String.format(Locale.US, "%.0f kg", state.recordVolume)
                        ViewMode.EXERCISE -> String.format(Locale.US, "%.1f kg", state.recordWeight)
                    },
                    subtitle = when (state.viewMode) {
                        ViewMode.GENERAL -> stringResource(R.string.progress_total_kg)
                        ViewMode.TRAINING -> stringResource(R.string.progress_best_session)
                        ViewMode.EXERCISE -> stringResource(R.string.progress_max_weight_record)
                    },
                    icon = when (state.viewMode) {
                        ViewMode.GENERAL -> Icons.Default.LocalFireDepartment
                        ViewMode.TRAINING -> Icons.Default.EmojiEvents
                        ViewMode.EXERCISE -> Icons.Default.FitnessCenter
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gráfico Principal
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                tonalElevation = 1.dp,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    val chartTitle = when (state.viewMode) {
                        ViewMode.GENERAL -> stringResource(R.string.progress_chart_title_frequency)
                        ViewMode.TRAINING -> stringResource(R.string.progress_chart_title_training_volume, state.selectedTraining?.title ?: "")
                        ViewMode.EXERCISE -> stringResource(R.string.progress_chart_title_exercise_evolution, state.selectedExercise?.name ?: "")
                    }

                    ChartHeader(
                        title = chartTitle,
                        percentage = state.percentageChange,
                        showPercentage = state.viewMode == ViewMode.EXERCISE,
                        granularity = state.chartGranularity,
                        onGranularityChange = { viewModel.onGranularityChanged(it) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(180.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else if (state.chartPoints.isNotEmpty()) {
                        LoadEvolutionChart(
                            points = state.chartPoints,
                            labels = state.chartLabels
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.progress_no_data),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seleção de Treino
            Text(
                text = stringResource(R.string.progress_select_training),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.outline
            )

            LazyRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedTraining == null,
                        onClick = { viewModel.onTrainingSelected(null) },
                        label = { Text(stringResource(R.string.progress_filter_all)) }
                    )
                }
                items(state.allTrainings) { training ->
                    FilterChip(
                        selected = state.selectedTraining?.id == training.id,
                        onClick = { viewModel.onTrainingSelected(training) },
                        label = { Text(training.title) }
                    )
                }
            }

            // Seleção de Exercício (Condicional)
            state.selectedTraining?.let { training ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.progress_exercises_of_training, training.title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline
                )

                LazyRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.exercisesOfSelectedTraining) { exercise ->
                        FilterChip(
                            selected = state.selectedExercise?.id == exercise.id,
                            onClick = { viewModel.onExerciseSelected(exercise) },
                            label = { Text(exercise.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun ChartHeader(
    title: String,
    percentage: Double,
    showPercentage: Boolean,
    granularity: ChartGranularity,
    onGranularityChange: (ChartGranularity) -> Unit
) {
    val color = if (percentage >= 0) Color(0xFF00796B) else Color(0xFFD32F2F)
    val icon =
        if (percentage >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
    val sign = if (percentage >= 0) "+" else ""
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showPercentage) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$sign${percentage.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = stringResource(R.string.progress_filter_label),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.progress_granularity_weekly)) },
                            onClick = {
                                onGranularityChange(ChartGranularity.WEEKLY)
                                showMenu = false
                            },
                            trailingIcon = {
                                if (granularity == ChartGranularity.WEEKLY) {
                                    Icon(Icons.Default.FitnessCenter, null, modifier = Modifier.size(16.dp))
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.progress_granularity_monthly)) },
                            onClick = {
                                onGranularityChange(ChartGranularity.MONTHLY)
                                showMenu = false
                            },
                            trailingIcon = {
                                if (granularity == ChartGranularity.MONTHLY) {
                                    Icon(Icons.Default.FitnessCenter, null, modifier = Modifier.size(16.dp))
                                }
                            }
                        )
                    }
                }
            }
        }
        
        Text(
            text = if (granularity == ChartGranularity.WEEKLY) 
                stringResource(R.string.progress_granularity_weekly) 
            else 
                stringResource(R.string.progress_granularity_monthly),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}
