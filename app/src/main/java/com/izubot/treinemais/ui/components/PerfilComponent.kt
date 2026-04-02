package com.izubot.treinemais.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
@Preview
fun PerfilComponent(
    modifier: Modifier = Modifier,
    imageUrl: String = "",
    userName: String = "",
    size: Dp = 56.dp,
    onPerfilClick: () -> Unit = {},
    editable: Boolean = false
) {
    val avatarFallback = "https://ui-avatars.com/api/?name=${userName}&size=512"

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.BottomEnd
    ) {
        IconButton(
            onClick = onPerfilClick,
            modifier = modifier
                .size(size)
                .aspectRatio(1f),
        ) {
            AsyncImage(
                model = imageUrl.ifBlank { avatarFallback },
                contentDescription = "Imagem de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                error = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                placeholder = ColorPainter(Color.Gray),
            )
        }

        if (editable) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(size * 0.05f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddAPhoto,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
