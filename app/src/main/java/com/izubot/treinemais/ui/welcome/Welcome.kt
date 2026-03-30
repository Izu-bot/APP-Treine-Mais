package com.izubot.treinemais.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.ButtonComponent


@Composable
fun Welcome(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val versionName = try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName
    } catch (e: Exception) {
        e.printStackTrace()
    }

    Column(modifier = modifier
        .fillMaxSize()
    ) {
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
                        shape = RoundedCornerShape(58.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = stringResource(R.string.app_name),
                    tint = MaterialTheme.colorScheme.primary,
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
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.undraw_morning_workout_73u9),
                    contentDescription = stringResource(R.string.welcome_image_content_descriptor),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(66.dp))

            Text(
                text = stringResource(R.string.welcome_call_to_action),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.welcome_subtitle),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(68.dp))

            ButtonComponent(
                onClick = {
                    onNavigateToRegister()
                },
                text = R.string.welcome_sign_up,
                style = MaterialTheme.typography.bodyLarge,
                weight = FontWeight.SemiBold,
                shape = 28.dp,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(52.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ButtonComponent(
                onClick = {
                    onNavigateToLogin()
                },
                text = R.string.welcome_sign_in,
                style = MaterialTheme.typography.bodyLarge,
                weight = FontWeight.SemiBold,
                shape = 28.dp,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(52.dp),
                borderStroke = BorderStroke(
                    (1.6).dp,
                    MaterialTheme.colorScheme.secondary
                    )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(
                    R.string.app_version,
                    versionName.toString())
                    .uppercase(),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 3.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
