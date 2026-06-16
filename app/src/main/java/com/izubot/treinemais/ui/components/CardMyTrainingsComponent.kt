package com.izubot.treinemais.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.ui.theme.TreineMaisTheme

@Composable
fun CardMyTrainingsComponent(
    training: Training,
    isExpanded: Boolean,
    onToggleCard: (String) -> Unit,
    modifier: Modifier = Modifier,
    onExerciseClick: (Exercise) -> Unit = {},
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "Expansion Arrow Rotation"
    )

    val exercisesToShow = if (isExpanded) training.exercises else training.exercises.take(2)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            )
            .clickable { onToggleCard(training.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = training.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Recolher" else "Expandir",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .rotate(rotationState),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Badge
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "${training.exercises.size} Exercícios",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }

            // Exercise List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                exercisesToShow.forEach { exercise ->
                    ExerciseItemComponent(
                        exercise = exercise,
                        onClick = { onExerciseClick(exercise) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun CardMyTrainingsLightPreview() {
    TreineMaisTheme(darkTheme = false, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            MockCardContent(isExpanded = false)
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun CardMyTrainingsDarkPreview() {
    TreineMaisTheme(darkTheme = true, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            MockCardContent(isExpanded = true)
        }
    }
}

@Composable
private fun MockCardContent(isExpanded: Boolean = false) {
    val mockTraining = Training(
        id = "1",
        title = "Peito",
        exercises = listOf(
            Exercise(id = "1", name = "Supino Reto", sets = 4, reps = "10-12 reps"),
            Exercise(id = "2", name = "Crucifixo Inclinado", sets = 3, reps = "15 reps"),
            Exercise(id = "3", name = "Cross Over", sets = 3, reps = "Falha"),
            Exercise(id = "4", name = "Supino Inclinado", sets = 3, reps = "12 reps")
        )
    )
    
    Box(modifier = Modifier.padding(16.dp)) {
        CardMyTrainingsComponent(
            training = mockTraining,
            isExpanded = isExpanded,
            onToggleCard = {}
        )
    }
}
