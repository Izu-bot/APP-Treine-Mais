package com.izubot.treinemais.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp

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
    borderStroke: BorderStroke? = null,
    enabled: Boolean = true,
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
        Text(
            text = stringResource(text),
            style = style,
            fontFamily = family,
            fontWeight = weight
        )
    }
}