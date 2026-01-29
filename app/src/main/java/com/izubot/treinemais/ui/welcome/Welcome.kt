package com.izubot.treinemais.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.theme.manropeFamily

@Composable
fun Welcome(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(14.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = stringResource(R.string.app_name),
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.app_name),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                fontFamily = manropeFamily,
                modifier = Modifier.align(
                    Alignment.CenterVertically
                )
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome_call_to_action),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                fontFamily = manropeFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.welcome_subtitle),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                fontFamily = manropeFamily
            )
        }
    }
}
