package com.izubot.treinemais.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.theme.manropeFamily
import kotlinx.coroutines.delay

@Composable
fun Splash(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {

    val currentOnSplashFinished by rememberUpdatedState(onSplashFinished)

    // Enquanto a API não é implementada
    LaunchedEffect(key1 = true) {
        delay(2500L)
        currentOnSplashFinished()
    }

    Column(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(180.dp)
            )
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSecondary,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center,
                fontFamily = manropeFamily
            )
            Text(
                text = stringResource(R.string.subtitle_splash).uppercase(),
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                fontFamily = manropeFamily
            )
            Spacer(modifier = Modifier.height(160.dp))
        }

        Text(
            text = stringResource(R.string.init).uppercase(),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.scrim,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 10.dp),
            fontFamily = manropeFamily
        )

        /*
        * TODO
        *  Make progress
        *  Só é possivel fazer quando tiver a lógica de conexão com a API
        * */
        LinearProgressIndicator(
            trackColor = MaterialTheme.colorScheme.secondary,
            color = MaterialTheme.colorScheme.primary,
            gapSize = 8.dp,
            modifier = Modifier
                .width(80.dp)
                .padding(bottom = 12.dp)
        )
    }
}
