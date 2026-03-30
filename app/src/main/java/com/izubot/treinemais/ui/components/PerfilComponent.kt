package com.izubot.treinemais.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
@Preview
fun PerfilComponent(
    modifier: Modifier = Modifier,
    imageUrl: String = "",
    size: Dp = 30.dp,
    onPerfilClick: () -> Unit = {}
) {
    IconButton(
        onClick = onPerfilClick,
        modifier = modifier
            .size(size * 2)
            .aspectRatio(1f)
    ) {
        /* TODO: Adicionar uma imagem mockada ou carregar a imagem a partir da URL */
        if (imageUrl.isBlank()) {
            Image(
                imageVector = Icons.Outlined.PersonOutline,
                contentDescription = "Imagem de perfil",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagem de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}