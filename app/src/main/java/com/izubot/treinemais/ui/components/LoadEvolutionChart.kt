package com.izubot.treinemais.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadEvolutionChart(
    points: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val labelStyle = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.outline,
        fontSize = 10.sp
    )

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 8.dp)
        ) {
            val width = size.width
            val height = size.height

            val gridLines = 3
            for (i in 0..gridLines) {
                val y = (height / gridLines) * i
                drawLine(
                    color = outlineColor.copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            if (points.isEmpty()) return@Canvas

            if (points.size == 1) {
                val x = width / 2
                val y = height - (points[0] * (height * 0.8f) + (height * 0.1f))
                drawCircle(
                    color = primaryColor,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = primaryColor.copy(alpha = 0.3f),
                    radius = 10.dp.toPx(),
                    center = Offset(x, y)
                )
                return@Canvas
            }

            val spacing = width / (points.size - 1)

            val strokePath = Path().apply {
                for (i in points.indices) {
                    val x = i * spacing
                    val y = height - (points[i] * (height * 0.8f) + (height * 0.1f))

                    if (i == 0) {
                        moveTo(x, y)
                    } else {
                        val prevX = (i - 1) * spacing
                        val prevY = height - (points[i - 1] * (height * 0.8f) + (height * 0.1f))

                        cubicTo(
                            x1 = prevX + spacing / 2, y1 = prevY,
                            x2 = prevX + spacing / 2, y2 = y,
                            x3 = x, y3 = y
                        )
                    }
                }
            }

            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.2f), Color.Transparent)
                )
            )

            drawPath(
                path = strokePath,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            val lastX = (points.size - 1) * spacing
            val lastY = height - (points.last() * (height * 0.8f) + (height * 0.1f))
            drawCircle(
                color = primaryColor,
                radius = 5.dp.toPx(),
                center = Offset(lastX, lastY)
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.3f),
                radius = 10.dp.toPx(),
                center = Offset(lastX, lastY)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            labels.forEachIndexed { _, label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    style = labelStyle,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}