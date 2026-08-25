package com.izubot.treinemais.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.domain.model.DayProgress
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.utils.UiEvent
import java.time.format.TextStyle

@Composable
fun Home(
    onNavigateToTrainingLog: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val state by homeViewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        homeViewModel.channel.collect { event ->
            when (event) {
                is UiEvent.Success -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Error -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Info -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

//    if (state.showFeedbackBottomSheet) {
//        FeedbackBottomSheet(
//            onDismiss = { homeViewModel.onFeedbackDismissed() },
//            onSubmit = { feature, performance, navigation, recommend ->
//                homeViewModel.onFeedbackSubmitted(feature, performance, navigation, recommend)
//            }
//        )
//    }

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
                            onNavigateToTrainingLog(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickTrainingSelection(
    trainings: List<Training>,
    onTrainingSelected: (String) -> Unit
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = trainings,
                    key = { it.id }
                ) { training ->
                    ElevatedCard(
                        onClick = { onTrainingSelected(training.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.height(90.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = {
                                Text(
                                    text = training.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .wrapContentHeight(
                                            align = Alignment.CenterVertically
                                        )
                                )
                            },
                            trailingContent = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.start_training),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FeedbackBottomSheet(
//    onDismiss: () -> Unit,
//    onSubmit: (String, String, String, String) -> Unit
//) {
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    var featureAnswer by remember { mutableStateOf("") }
//    var performanceAnswer by remember { mutableStateOf("") }
//    var navigationAnswer by remember { mutableStateOf("") }
//    var recommendAnswer by remember { mutableStateOf("") }
//
//    ModalBottomSheet(
//        onDismissRequest = onDismiss,
//        sheetState = sheetState,
//        containerColor = MaterialTheme.colorScheme.surface,
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 24.dp)
//                .padding(bottom = 32.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Text(
//                text = stringResource(R.string.feedback_title),
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold
//            )
//            Text(
//                text = stringResource(R.string.feedback_subtitle),
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//
//            HorizontalDivider()
//
//            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(20.dp),
//                modifier = Modifier.weight(1f, fill = false)
//            ) {
//                item {
//                    FeedbackQuestionField(
//                        question = stringResource(R.string.feedback_question_1),
//                        value = featureAnswer,
//                        onValueChange = { featureAnswer = it }
//                    )
//                }
//                item {
//                    FeedbackQuestionField(
//                        question = stringResource(R.string.feedback_question_2),
//                        value = performanceAnswer,
//                        onValueChange = { performanceAnswer = it }
//                    )
//                }
//                item {
//                    FeedbackQuestionField(
//                        question = stringResource(R.string.feedback_question_3),
//                        value = navigationAnswer,
//                        onValueChange = { navigationAnswer = it }
//                    )
//                }
//                item {
//                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                        Text(
//                            text = stringResource(R.string.feedback_question_4),
//                            style = MaterialTheme.typography.titleSmall,
//                            fontWeight = FontWeight.SemiBold
//                        )
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(16.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            FeedbackOption(
//                                label = stringResource(R.string.feedback_option_yes),
//                                selected = recommendAnswer == "Sim",
//                                onClick = { recommendAnswer = "Sim" }
//                            )
//                            FeedbackOption(
//                                label = stringResource(R.string.feedback_option_no),
//                                selected = recommendAnswer == "Não",
//                                onClick = { recommendAnswer = "Não" }
//                            )
//                        }
//                    }
//                }
//            }
//
//            Button(
//                onClick = {
//                    onSubmit(featureAnswer, performanceAnswer, navigationAnswer, recommendAnswer)
//                },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = featureAnswer.isNotBlank() || performanceAnswer.isNotBlank() ||
//                        navigationAnswer.isNotBlank() || recommendAnswer.isNotBlank()
//            ) {
//                Text(stringResource(R.string.feedback_button_send))
//            }
//        }
//    }
//}
//
//@Composable
//fun FeedbackQuestionField(
//    question: String,
//    value: String,
//    onValueChange: (String) -> Unit
//) {
//    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//        Text(
//            text = question,
//            style = MaterialTheme.typography.titleSmall,
//            fontWeight = FontWeight.SemiBold
//        )
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//            modifier = Modifier.fillMaxWidth(),
//            placeholder = { Text(stringResource(R.string.feedback_placeholder_answer)) },
//            shape = MaterialTheme.shapes.medium
//        )
//    }
//}
//
//@Composable
//fun FeedbackOption(
//    label: String,
//    selected: Boolean,
//    onClick: () -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.clickable { onClick() }
//    ) {
//        RadioButton(selected = selected, onClick = onClick)
//        Text(text = label, style = MaterialTheme.typography.bodyMedium)
//    }
//}

@Composable
fun DayItem(day: DayProgress) {
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    val dayLetter = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
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
