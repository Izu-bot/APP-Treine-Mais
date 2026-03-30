package com.izubot.treinemais.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun CardTraining(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    containerCardColor: Color = MaterialTheme.colorScheme.primary,
    contentCardColor: Color = MaterialTheme.colorScheme.secondary,
    elevationCard: CardElevation = CardDefaults.cardElevation(),
    @StringRes trainingToday: Int = 0,
    title: String = "",
    marginSpacing: Dp = 0.dp,
    exercises: String = "",
    content: @Composable () -> Unit = {}
) {
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerCardColor,
            contentColor = contentCardColor
        ),
        elevation = elevationCard,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(26.dp)
        ) {
            Text(
                text = stringResource(trainingToday),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(marginSpacing))

            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FitnessCenter,
                    modifier = Modifier.size(20.dp),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = exercises,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(modifier = Modifier.height(marginSpacing))
            content()
        }
    }
}
