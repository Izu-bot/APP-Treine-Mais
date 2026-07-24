package com.izubot.treinemais.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.izubot.treinemais.R

@Composable
fun OutlinedTextFieldComponent(
    modifier: Modifier = Modifier,
    value: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    onValueChange: (String) -> Unit,
    labelText: String,
    leadingIcon: ImageVector? = null,
    color: TextFieldColors,
    placeholderText: String,
    keyboardActions: KeyboardActions? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    errorMessage: Int? = null,
    isPasswordField: Boolean = false,
    isPasswordVisible: Boolean = false,
    isUiLogin: Boolean = false,
    shape: Dp = 26.dp,
    onVisibilityChange: () -> Unit = {},
    isError: Boolean = false,
    readOnly: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    
    val defaultKeyboardActions = remember {
        KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Next) },
            onDone = { focusManager.clearFocus() },
            onPrevious = { focusManager.moveFocus(FocusDirection.Previous) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = labelText,
            textAlign = TextAlign.Start,
            style = textStyle,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            textStyle = textStyle,
            onValueChange = onValueChange,
            placeholder = { Text(placeholderText) },
            singleLine = true,
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null // Decorative icon
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            visualTransformation = if (isPasswordField && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPasswordField) {
                    val icon = if (isPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                    val description = if (isPasswordVisible) "Esconder senha" else "Mostrar senha"
                    IconButton(onClick = onVisibilityChange) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            },
            shape = RoundedCornerShape(shape),
            colors = color,
            isError = isError,
            supportingText = {
                if (isError) {
                    val resId = if (errorMessage != null && errorMessage != 0) errorMessage else R.string.error_generic
                    Text(
                        text = stringResource(resId),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardActions = keyboardActions ?: defaultKeyboardActions,
            keyboardOptions = keyboardOptions
        )
        if (isPasswordField && isUiLogin) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.login_forgot_password),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}