package com.izubot.treinemais.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.izubot.treinemais.ui.theme.TreineMaisTheme

@Composable
fun EditableExerciseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String = "",
    suffix: String = "",
    maxLines: Int = 1,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = {
            Text(labelText)
        },
        maxLines = maxLines,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        readOnly = readOnly,
        enabled = enabled,
        suffix = { Text(suffix) }
    )
}


@Preview(showBackground = true, name = "Light Mode")
@Composable
fun EditableExerciseTextFieldLightPreview() {
    TreineMaisTheme(darkTheme = false, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditableExerciseTextField(
                value = "",
                onValueChange = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "With Label")
@Composable
fun EditableExerciseTextFieldWithLabelPreview() {
    TreineMaisTheme(darkTheme = false, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditableExerciseTextField(
                value = "10",
                onValueChange = {},
                labelText = "Sets",
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun EditableExerciseTextFieldDarkPreview() {
    TreineMaisTheme(darkTheme = true, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditableExerciseTextField(
                value = "",
                onValueChange = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun EditableExerciseTextFieldLightWithTextPreview() {
    TreineMaisTheme(darkTheme = false, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditableExerciseTextField(
                value = "90",
                onValueChange = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun EditableExerciseTextFieldDarkWithTextPreview() {
    TreineMaisTheme(darkTheme = true, dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditableExerciseTextField(
                value = "12",
                onValueChange = {},
            )
        }
    }
}

