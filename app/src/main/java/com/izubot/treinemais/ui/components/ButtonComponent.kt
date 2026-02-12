package com.izubot.treinemais.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ButtonComponent(
    onClick: () -> Unit,
    @StringRes text: Int,
    style: TextStyle,
    family: FontFamily,
    weight: FontWeight,
    shape: Dp,
    elevation: ButtonElevation,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    colorIcon: Color = MaterialTheme.colorScheme.primary,
    borderStroke: BorderStroke? = null,
    enabled: Boolean = true,
    imageVector: ImageVector? = null
) {
    ElevatedButton(
        onClick = onClick,
        shape = RoundedCornerShape(shape),
        elevation = elevation,
        enabled = enabled,
        border = borderStroke,
        modifier = modifier,
        colors = colors,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(text),
                style = style,
                fontFamily = family,
                fontWeight = weight
            )

            if (imageVector != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    imageVector = imageVector,
                    contentDescription = stringResource(text),
                    colorFilter = ColorFilter.tint(colorIcon)
                )
            }
        }
    }
}