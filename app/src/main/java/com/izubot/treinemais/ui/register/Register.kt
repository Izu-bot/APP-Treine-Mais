package com.izubot.treinemais.ui.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.ButtonComponent
import com.izubot.treinemais.ui.components.OutlinedTextFieldComponent
import com.izubot.treinemais.ui.theme.manropeFamily

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Register(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.totalSteps })

    LaunchedEffect(uiState.currentStep) {
        pagerState.animateScrollToPage(uiState.currentStep)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { viewModel.onPreviousStep() }
            )
            StepIndicator(
                modifier = Modifier.align(Alignment.Center),
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.Top,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> Credentials(
                    email = uiState.email,
                    password = uiState.password,
                    confirmPassword = uiState.confirmPassword,
                    passwordVisible = uiState.passwordVisibility,
                    confirmPasswordVisible = uiState.confirmPasswordVisibility,
                    toggleConfirmPasswordVisibility = { viewModel.toggleConfirmPasswordVisibility() },
                    togglePasswordVisibility = { viewModel.togglePasswordVisibility() },
                    onEmailChange = { viewModel.onEmailChange(it) },
                    onPasswordChange = { viewModel.onPasswordChange(it) },
                    onConfirmPasswordChange = { viewModel.onConfirmPasswordChange(it) },
                    isPasswordError = uiState.passwordError
                )
                1 -> StepContent(text = "Passo 2: A ser implementado")
                2 -> StepContent(text = "Passo 3: A ser implementado")
            }
        }

        ButtonComponent(
            onClick = { viewModel.onNextStep() },
            text = if (uiState.currentStep < uiState.totalSteps - 1) R.string.register_button_next else R.string.register_button_finish,
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

@Composable
fun StepIndicator(
    modifier: Modifier = Modifier,
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0 until totalSteps) {
            val color = if (i <= currentStep) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun Credentials(
    modifier: Modifier = Modifier,
    email: String = "",
    password: String = "",
    confirmPassword: String = "",
    passwordVisible: Boolean = false,
    togglePasswordVisibility: () -> Unit = {},
    confirmPasswordVisible: Boolean = false,
    toggleConfirmPasswordVisibility: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    isPasswordError: Boolean = false
) {
    Column(modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 36.dp)
    ) {

        Text(
            text = stringResource(R.string.register_create_account),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = manropeFamily
        )

        Text(
            text = stringResource(R.string.register_access_credentials),
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = manropeFamily,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextFieldComponent(
            value = email,
            onValueChange = onEmailChange,
            isPasswordField = false,
            labelText = stringResource(R.string.login_email),
            leadingIcon = Icons.Rounded.Email,
            placeholderText = stringResource(R.string.register_placeholder_email),
            shape = 8.dp,
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
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextFieldComponent(
            value = password,
            onValueChange = onPasswordChange,
            isPasswordField = true,
            isPasswordVisible = passwordVisible,
            onVisibilityChange = togglePasswordVisibility,
            labelText = stringResource(R.string.login_password),
            leadingIcon = Icons.Rounded.Lock,
            placeholderText = stringResource(R.string.register_placeholder_password),
            shape = 8.dp,
            isError = isPasswordError,
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
            )

        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextFieldComponent(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            isPasswordField = true,
            isPasswordVisible = confirmPasswordVisible,
            onVisibilityChange = toggleConfirmPasswordVisibility,
            labelText = stringResource(R.string.register_confirm_password),
            leadingIcon = Icons.Rounded.Lock,
            placeholderText = stringResource(R.string.register_placeholder_password),
            shape = 8.dp,
            isError = isPasswordError,
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
            )
        )
        if (isPasswordError) {
            Text(
                text = stringResource(R.string.register_password_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun PersonalProfile() {

}

@Composable
fun StepContent(text: String) {
    // Here you can define the actual content for each step of your form
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}