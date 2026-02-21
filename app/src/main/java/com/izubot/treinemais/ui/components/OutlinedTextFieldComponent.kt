package com.izubot.treinemais.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
    onValueChange: (String) -> Unit,
    labelText: String,
    leadingIcon: ImageVector,
    color: TextFieldColors,
    isPasswordField: Boolean = false,
    isPasswordVisible: Boolean = false,
    isUiLogin: Boolean = false,
    shape: Dp = 26.dp,
    onVisibilityChange: () -> Unit = {},
    placeholderText: String,
    isError: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = labelText,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholderText) },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null // Decorative icon
                )
            },
            modifier = Modifier.fillMaxWidth(),
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
            isError = isError
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

/*
* TODO
*  Falta colocar as interações do teclado
*  Colocar também a mudança de foco
* */