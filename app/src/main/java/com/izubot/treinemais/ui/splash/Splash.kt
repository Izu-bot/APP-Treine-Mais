package com.izubot.treinemais.ui.splash

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R

@Composable
@Preview
fun Splash(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean? = null,
    onSplashFinished: () -> Unit = {},
    splashViewModel: SplashViewModel = hiltViewModel<SplashViewModel>()
) {

    val isReady by splashViewModel.isReady.collectAsState()

    LaunchedEffect(isReady, isLoggedIn) {
        if (isReady && isLoggedIn != null) {
            onSplashFinished()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
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
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.subtitle_splash).uppercase(),
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(160.dp))
        }

        Text(
            text = stringResource(R.string.init).uppercase(),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        /*
        * TODO
        *  Make progress
        *  Só é possivel fazer quando tiver a lógica de conexão com a API
        * */
        LinearProgressIndicator(
            trackColor = MaterialTheme.colorScheme.onPrimary,
            color = MaterialTheme.colorScheme.primary,
            gapSize = 8.dp,
            modifier = Modifier
                .width(80.dp)
                .padding(bottom = 12.dp)
        )
    }
}
