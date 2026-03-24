package com.izubot.treinemais.ui.home

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.ButtonComponent
import com.izubot.treinemais.ui.components.CardTraining
import com.izubot.treinemais.ui.components.HomeHeaderComponent
import com.izubot.treinemais.ui.components.WidgetComponent
import com.izubot.treinemais.ui.theme.manropeFamily

@Composable
@Preview(showSystemUi = true)
fun Home(
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val state by homeViewModel.state.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeHeaderComponent {}
        HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = (0.5).dp, color = MaterialTheme.colorScheme.tertiary)

        Spacer(modifier = Modifier.height(50.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row {
                Text(
                    text = homeViewModel.greet(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = state.nameUser,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.onSecondary.copy(1f),
                            offset = Offset(0f, 0f),
                            blurRadius = 30f
                        )
                    ),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = manropeFamily
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            CardTraining(
                shape = MaterialTheme.shapes.extraLarge,
                containerCardColor = MaterialTheme.colorScheme.onSecondary,
                contentCardColor = MaterialTheme.colorScheme.primary,
                trainingToday = R.string.todays_training,
                title = "Upper Body Power",
                marginSpacing = 20.dp,
                exercises = "12 exercises",
                content = {
                    ButtonComponent(
                        onClick = { },
                        text = R.string.todays_training,
                        style = MaterialTheme.typography.labelLarge,
                        family = manropeFamily,
                        weight = FontWeight.SemiBold,
                        shape = 28.dp,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 0.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSecondary,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(height = 60.dp, width = 0.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                WidgetComponent(
                icon = Icons.Default.FitnessCenter,
                label = "Weight",
                title = "+2",
                subTitle = "vs last month"
                )

                WidgetComponent(
                    icon = Icons.Default.FitnessCenter,
                    label = "Weight",
                    title = "+2",
                    subTitle = "vs last month"
                )
            }
        }
    }
}
