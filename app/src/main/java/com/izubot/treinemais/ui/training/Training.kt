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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.domain.model.Exercise
import com.izubot.treinemais.domain.model.Training
import com.izubot.treinemais.ui.components.CardMyTrainingsComponent

@Composable
fun Training(
    modifier: Modifier = Modifier,
    trainingViewModel: TrainingViewModel = hiltViewModel<TrainingViewModel>()
    ) {

    val state by trainingViewModel.state.collectAsState()

    val exercises = listOf(
        Exercise(id = "1", name = "Supino Reto", sets = 4, reps = "10-12 reps"),
        Exercise(id = "2", name = "Crucifixo Inclinado", sets = 3, reps = "15 reps"),
        Exercise(id = "3", name = "Cross Over", sets = 3, reps = "Falha"),
        Exercise(id = "4", name = "Supino Reto", sets = 4, reps = "10-12 reps"),
        Exercise(id = "5", name = "Crucifixo Inclinado", sets = 3, reps = "15 reps"),
    )

    // Lista de treinos com IDs diferentes para testar a expansão individual
    val listaDeTreinos = listOf(
        Training(id = "peito", title = "Peito", exercises = exercises),
        Training(id = "costas", title = "Costas", exercises = exercises),
        Training(id = "pernas", title = "Pernas", exercises = exercises),
        Training(id = "ombros", title = "Ombros", exercises = exercises)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Adicionar novo treino */ }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar treino")
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
                    text = "Meus Treinos",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Gerencie sua rotina semanal por grupos musculares.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listaDeTreinos) { treino ->
                    val isExpanded = state.expandedCardIds.contains(treino.id)
                    CardMyTrainingsComponent(
                        training = treino,
                        isExpanded = isExpanded,
                        onToggleCard = { trainingViewModel.onToggleCard(it) }
                    )
                }
            }
        }
    }
}
