package com.izubot.treinemais.ui.confirm

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.ButtonComponent
import com.izubot.treinemais.ui.theme.TreineMaisTheme
import com.izubot.treinemais.ui.theme.manropeFamily

@Composable
fun Confirm(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.confirm_email),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = manropeFamily,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 30.sp
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(260.dp)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.undraw_mail_sent_ujev),
                contentDescription = stringResource(R.string.confirm_email),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.confirm_email_subtitle),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleSmall,
            fontFamily = manropeFamily,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onTertiary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ButtonComponent(
            onClick = {
                val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.gm")
                if (intent != null) {
                    context.startActivity(intent)
                } else {
                    val emailIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(emailIntent)
                }
            },
            text = R.string.go_to_gmail,
            style = MaterialTheme.typography.bodyLarge,
            family = manropeFamily,
            weight = FontWeight.SemiBold,
            shape = 28.dp,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            imageVector = Icons.Rounded.MailOutline,
            colorIcon = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfirmPreview() {
    TreineMaisTheme {
        Confirm()
    }
}