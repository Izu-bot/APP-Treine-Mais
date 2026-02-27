package com.izubot.treinemais.ui.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.VolunteerActivism
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.ButtonComponent
import com.izubot.treinemais.ui.components.GoalsCard
import com.izubot.treinemais.ui.components.OutlinedTextFieldComponent
import com.izubot.treinemais.ui.components.VisualCard
import com.izubot.treinemais.ui.theme.manropeFamily

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Register(
    onNavigateBack: () -> Unit,
    onNavigateToConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel<RegisterViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.totalSteps })
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }


    LaunchedEffect(uiState.currentStep) {
        pagerState.animateScrollToPage(uiState.currentStep)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                tint = MaterialTheme.colorScheme.onSecondary,
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable {
                        if (uiState.currentStep == 0) {
                            onNavigateBack()
                        } else {
                            viewModel.onPreviousStep()
                        }
                    }
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
                0 -> Goals(
                    selectedGoals = uiState.selectedGoals,
                    onGoalsSelected = { viewModel.onGoalsSelected(it) }
                )
                1 -> PersonalProfile(
                    name = uiState.name,
                    onNameChange = { viewModel.onNameChange(it) },
                    selectedGender = uiState.selectedGender,
                    onGenderSelected = { viewModel.onGenderSelected(it) },
                    isNameError = uiState.nameError,
                    errorNameMessage = uiState.errorNameMessage
                )
                2 -> Credentials(
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
                    isPasswordError = uiState.passwordError,
                    isEmailError = uiState.emailError,
                    errorPasswordMessage = uiState.errorPasswordMessage,
                    errorEmailMessage = uiState.errorEmailMessage
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSecondary
            )
        } else {
            ButtonComponent(
                onClick = {
                    when (uiState.currentStep) {
                        0 -> viewModel.onNextStep()
                        1 -> {
                            if (viewModel.onValidateName()) {
                                viewModel.onNextStep()
                            }
                        }
                        2 -> {
                            val isEmailValid = viewModel.onValidateEmail()
                            val isPasswordValid = viewModel.onValidatePassword()

                            if (isEmailValid && isPasswordValid) {
                                viewModel.register()
                                onNavigateToConfirm()
                            }
                        }
                    }
                },
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
                    .size(56.dp),
                imageVector = if (uiState.currentStep < uiState.totalSteps - 1) Icons.AutoMirrored.Rounded.ArrowForward else Icons.Rounded.DoneAll
            )
        }
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
    errorPasswordMessage: Int? = null,
    errorEmailMessage: Int? = null,
    passwordVisible: Boolean = false,
    togglePasswordVisibility: () -> Unit = {},
    confirmPasswordVisible: Boolean = false,
    toggleConfirmPasswordVisibility: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    isPasswordError: Boolean = false,
    isEmailError: Boolean = false
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
            isError = isEmailError,
            errorMessage = errorEmailMessage,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
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
            ),
            errorMessage = errorPasswordMessage,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
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
            ),
            errorMessage = errorPasswordMessage,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            )
        )
        Text(
            text = stringResource(R.string.register_password_verification),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.padding(start = 0.dp, top = 4.dp),
            fontFamily = manropeFamily,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalProfile(
    modifier: Modifier = Modifier,
    name: String = "",
    isNameError: Boolean = false,
    errorNameMessage: Int? = null,
    onNameChange: (String) -> Unit = {},
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit
) {
    Column(modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 36.dp)
    ) {
        Text(
            text = stringResource(R.string.register_personal_profile),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = manropeFamily
        )

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedTextFieldComponent(
            value = name,
            onValueChange = onNameChange,
            labelText = stringResource(R.string.register_call_you),
            leadingIcon = Icons.Rounded.Person,
            shape = 8.dp,
            placeholderText = stringResource(R.string.register_full_name),
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
            isError = isNameError,
            errorMessage = errorNameMessage,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.register_gender),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = manropeFamily,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = stringResource(R.string.register_gender_explication),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiary,
            fontFamily = manropeFamily,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VisualCard(
                title = R.string.register_gender_masculine,
                icon = Icons.Rounded.Male,
                onClick = { onGenderSelected(Gender.MALE) },
                isSelected = selectedGender == Gender.MALE
            )

            VisualCard(
                title = R.string.register_gender_feminine,
                icon = Icons.Rounded.Female,
                onClick = { onGenderSelected(Gender.FEMALE) },
                isSelected = selectedGender == Gender.FEMALE
            )

            VisualCard(
                title = R.string.register_gender_other,
                icon = Icons.Rounded.Accessibility,
                onClick = { onGenderSelected(Gender.OTHER) },
                isSelected = selectedGender == Gender.OTHER
            )
        }
    }
}

@Composable
fun Goals(
    selectedGoals: Goals?,
    modifier: Modifier = Modifier,
    onGoalsSelected: (Goals) -> Unit = {}
) {
    Column(modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 36.dp)
    ) {

        Text(
            text = stringResource(R.string.register_goals),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = manropeFamily
        )

        Text(
            text = stringResource(R.string.register_goals_explication),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiary,
            fontFamily = manropeFamily,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(36.dp))

        GoalsCard(
            title = R.string.register_goals_mass_gain,
            subtitle = R.string.register_goals_mass_gain,
            icon = Icons.Rounded.FitnessCenter,
            onClick = { onGoalsSelected(Goals.MASS_GAIN) },
            isSelected = selectedGoals == Goals.MASS_GAIN,
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 80.dp, width = 0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalsCard(
            title = R.string.register_goals_fat_loss,
            subtitle = R.string.register_goals_mass_gain,
            icon = Icons.AutoMirrored.Rounded.TrendingDown,
            onClick = { onGoalsSelected(Goals.FAT_LOSS) },
            isSelected = selectedGoals == Goals.FAT_LOSS,
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 80.dp, width = 0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalsCard(
            title = R.string.register_goals_health,
            subtitle = R.string.register_goals_mass_gain,
            icon = Icons.Rounded.VolunteerActivism,
            onClick = { onGoalsSelected(Goals.HEALTH) },
            isSelected = selectedGoals == Goals.HEALTH,
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 80.dp, width = 0.dp)
        )
    }
}