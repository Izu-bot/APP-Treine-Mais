package com.izubot.treinemais.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.ButtonDefaults
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
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.ButtonComponent
import com.izubot.treinemais.ui.theme.manropeFamily

@Composable
fun Login(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBackIosNew,
            contentDescription = stringResource(R.string.arrow_back_welcome),
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
        )
        Text(
            text = stringResource(R.string.title_login),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 60.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .background(
                    MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.FitnessCenter,
                contentDescription = stringResource(R.string.app_name),
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier.size(90.dp)
            )
        }
        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = stringResource(R.string.welcome_login),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = manropeFamily,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.welcome_login_subtile),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            fontFamily = manropeFamily,
            fontWeight = FontWeight.Medium
        )


        Spacer(modifier = Modifier.height(62.dp))
        ButtonComponent(
            onClick = {},
            text = R.string.login_enter,
            style = MaterialTheme.typography.bodyLarge,
            family = manropeFamily,
            weight = FontWeight.Bold,
            shape = 20.dp,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 16.dp,
                pressedElevation = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            imageVector = Icons.AutoMirrored.Rounded.Login,
            colorIcon = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp).size(56.dp)
        )
    }
}
