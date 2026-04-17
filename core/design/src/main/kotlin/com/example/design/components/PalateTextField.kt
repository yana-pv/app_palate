package com.example.design.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.design.theme.PalateColors

@Composable
fun PalateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelResId: Int,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelResId)) },
        modifier = modifier,
        singleLine = true,
        isError = isError,
        visualTransformation = when {
            isPassword && !passwordVisible -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible)
                                android.R.drawable.ic_menu_close_clear_cancel
                            else
                                android.R.drawable.ic_menu_view
                        ),
                        contentDescription = if (passwordVisible)
                            "Скрыть пароль"
                        else
                            "Показать пароль"
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PalateColors.GreenPrimary,
            unfocusedBorderColor = PalateColors.GrayBorder,
            focusedLabelColor = PalateColors.GreenPrimary,
            unfocusedLabelColor = PalateColors.GrayText,
            focusedTextColor = PalateColors.Black,
            unfocusedTextColor = PalateColors.Black,
            cursorColor = PalateColors.GreenPrimary
        )
    )
}