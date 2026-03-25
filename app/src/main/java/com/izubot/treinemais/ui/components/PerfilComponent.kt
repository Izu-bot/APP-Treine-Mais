package com.izubot.treinemais.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
@Preview
fun PerfilComponent(
    imageUrl: String = "",
    onPerfilClick: () -> Unit = {}
) {
    IconButton(
        onClick = onPerfilClick,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 0.dp)
    ) {
        AsyncImage(
            model = imageUrl.ifBlank {
                Image(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = "Image de perfil",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            },
            contentDescription = "Imagem de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary ,CircleShape)
        )
    }
}