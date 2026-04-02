package com.izubot.treinemais.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.izubot.treinemais.R
import com.izubot.treinemais.ui.components.OutlinedTextFieldComponent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfo(
    state: ProfileUiState,
    onChangeName: (String) -> Unit = {},
    onChangeEmail: (String) -> Unit = {},
    onDismissDatePicker: () -> Unit = {},
    onDateSelected: (LocalDate) -> Unit = {},
    onShowDatePicker: () -> Unit = {},
) {
    val datePickerState = rememberDatePickerState()

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismissDatePicker,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(selectedDate)
                        }
                        onDismissDatePicker()
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismissDatePicker) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.personal_information),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.inverseSurface
        )

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextFieldComponent(
            value = state.name,
            onValueChange = onChangeName,
            isPasswordField = false,
            labelText = stringResource(R.string.profile_name),
            leadingIcon = Icons.Rounded.Person,
            placeholderText = "",
            shape = 8.dp,
            textStyle = MaterialTheme.typography.bodyLarge,
            color = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )

        OutlinedTextFieldComponent(
            value = state.email,
            onValueChange = onChangeEmail,
            isPasswordField = false,
            labelText = stringResource(R.string.profile_email),
            leadingIcon = Icons.Rounded.Person,
            placeholderText = "",
            shape = 8.dp,
            textStyle = MaterialTheme.typography.bodyLarge,
            color = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )

        OutlinedTextFieldComponent(
            value = if (state.gender == null) "Não informado" else state.gender.toString(),
            onValueChange = {},
            isPasswordField = false,
            labelText = stringResource(R.string.profile_gender),
            leadingIcon = Icons.Rounded.Person,
            placeholderText = "",
            readOnly = true,
            shape = 8.dp,
            textStyle = MaterialTheme.typography.bodyLarge,
            color = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextFieldComponent(
                value = if (state.birthDate == null) "Não informado"
                    else state.birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())),
                onValueChange = {},
                readOnly = true,
                isPasswordField = false,
                labelText = stringResource(R.string.profile_birthdate),
                leadingIcon = Icons.Rounded.Person,
                placeholderText = "",
                shape = 8.dp,
                textStyle = MaterialTheme.typography.bodyLarge,
                color = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.background,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0f)
                    .clickable { onShowDatePicker() }
            )
        }
    }
}
