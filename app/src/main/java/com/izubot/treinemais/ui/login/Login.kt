package com.izubot.treinemais.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.ButtonComponent
import com.izubot.treinemais.ui.components.OutlinedTextFieldComponent
import com.izubot.treinemais.ui.theme.manropeFamily

@Composable
fun Login(
    onNavigateToWelcome: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    token: String? = null,
    loginViewModel: LoginViewModel = hiltViewModel<LoginViewModel>()
) {
    val state by loginViewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current


    LaunchedEffect(token) {
        if (token != null) {
            loginViewModel.confirmEmail(token)
        }
    }

    LaunchedEffect(Unit) { loginViewModel.toastEvent.collect { event ->
        when (event) {
            is String -> {
                Toast.makeText(context, event, Toast.LENGTH_SHORT).show()
            }
        }
    } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                keyboardController?.hide()
                focusManager.clearFocus()
            },
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = stringResource(R.string.arrow_back_welcome),
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickable {
                        onNavigateToWelcome()
                    }
            )
            Text(
                text = stringResource(R.string.title_login),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Body
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
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
                text = stringResource(R.string.welcome_login_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                fontFamily = manropeFamily,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextFieldComponent(
                value = state.email,
                onValueChange = loginViewModel::onEmailChange,
                labelText = stringResource(R.string.login_email),
                leadingIcon = Icons.Rounded.Email,
                shape = 8.dp,
                placeholderText = stringResource(R.string.login_email_placeholder),
                color = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                    focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onTertiary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
                    cursorColor = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier.padding(horizontal = 12.dp),
                isError = state.emailError,
                errorMessage = state.errorEmailMessage,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextFieldComponent(
                value = state.password,
                onValueChange = loginViewModel::onPasswordChange,
                labelText = stringResource(R.string.login_password),
                leadingIcon = Icons.Rounded.Lock,
                placeholderText = stringResource(R.string.login_password_placeholder),
                shape = 8.dp,
                isPasswordField = true,
                isPasswordVisible = state.isPasswordVisible,
                onVisibilityChange = loginViewModel::onTogglePasswordVisibility,
                color = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                    focusedContainerColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onTertiary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
                    cursorColor = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier.padding(horizontal = 12.dp),
                isError = state.passwordError,
                errorMessage = state.errorPasswordMessage,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(62.dp))

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            else {
                ButtonComponent(
                    onClick = {
                        val emailVerify = loginViewModel.onValidateEmail()
                        val passwordVerify = loginViewModel.onValidatePassword()

                        if (emailVerify && passwordVerify) {
                            loginViewModel.login()
                            onLoginSuccess()
                        }
                    },

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 26.dp)
                        .size(56.dp)
                )
            }
        }
    }
}