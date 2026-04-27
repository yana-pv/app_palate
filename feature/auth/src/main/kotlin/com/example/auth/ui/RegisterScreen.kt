package com.example.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.auth.R
import com.example.navigation.Destination
import com.example.design.theme.CondimentFont


@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<Int?>(null) }
    var emailError by remember { mutableStateOf<Int?>(null) }
    var passwordError by remember { mutableStateOf<Int?>(null) }
    var confirmPasswordError by remember { mutableStateOf<Int?>(null) }

    val registerState by viewModel.registerState.collectAsState()

    fun validateFields(): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameError = R.string.error_name_required
            isValid = false
        }
        else {
            nameError = null
        }

        if (email.isBlank()) {
            emailError = R.string.error_email_required
            isValid = false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = R.string.error_email_invalid
            isValid = false
        }
        else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = R.string.error_password_required
            isValid = false
        }
        else if (password.length < 6) {
            passwordError = R.string.error_password_min
            isValid = false
        }
        else {
            passwordError = null
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = R.string.error_confirm_password_required
            isValid = false
        }
        else if (password != confirmPassword) {
            confirmPasswordError = R.string.error_password_mismatch
            isValid = false
        }
        else {
            confirmPasswordError = null
        }

        return isValid
    }

    if (registerState is RegisterUiState.Success) {
        LaunchedEffect(Unit) {
            navController.navigate(Destination.Home.route) {
                popUpTo(Destination.Startup.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.startup_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.auth_container_height))
                .align(Alignment.BottomCenter)
                .clip(
                    RoundedCornerShape(
                        topStart = dimensionResource(R.dimen.auth_container_radius),
                        topEnd = dimensionResource(R.dimen.auth_container_radius)
                    )
                )
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimensionResource(com.example.design.R.dimen.padding_large), vertical = dimensionResource(R.dimen.auth_vertical_padding)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(com.example.design.R.string.app_name),
                    fontFamily = CondimentFont,
                    fontSize = dimensionResource(R.dimen.auth_logo_size).value.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.tertiary,
                    letterSpacing = dimensionResource(R.dimen.auth_logo_letter_spacing).value.sp,
                    lineHeight = dimensionResource(R.dimen.auth_logo_line_height).value.sp
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.auth_vertical_padding)))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = dimensionResource(R.dimen.auth_field_border_width),
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(dimensionResource(R.dimen.auth_field_padding))
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (it.isNotBlank()) nameError = null
                        },
                        label = { Text(stringResource(R.string.name_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nameError != null,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (nameError != null) {
                        Text(
                            text = stringResource(nameError!!),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp,
                            modifier = Modifier.padding(start = dimensionResource(com.example.design.R.dimen.padding_small), top = dimensionResource(com.example.design.R.dimen.padding_small)))
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.auth_field_spacing)))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (it.isNotBlank()) emailError = null
                        },
                        label = { Text(stringResource(R.string.email_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = emailError != null,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (emailError != null) {
                        Text(
                            text = stringResource(emailError!!),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp,
                            modifier = Modifier.padding(start = dimensionResource(com.example.design.R.dimen.padding_small), top = dimensionResource(com.example.design.R.dimen.padding_small)))
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.auth_field_spacing)))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (it.isNotBlank()) passwordError = null
                        },
                        label = { Text(stringResource(R.string.password_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passwordError != null,
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (passwordVisible)
                                            com.example.design.R.drawable.eye_off
                                        else
                                            com.example.design.R.drawable.eye
                                    ),
                                    contentDescription = if (passwordVisible) stringResource(R.string.password_hide_content_desc) else stringResource(R.string.password_show_content_desc)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (passwordError != null) {
                        Text(
                            text = stringResource(passwordError!!),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp,
                            modifier = Modifier.padding(start = dimensionResource(com.example.design.R.dimen.padding_small), top = dimensionResource(com.example.design.R.dimen.padding_small)))
                    } else {
                        Text(
                            text = stringResource(R.string.password_min_hint),
                            fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = dimensionResource(com.example.design.R.dimen.padding_small), top = dimensionResource(com.example.design.R.dimen.padding_small))
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.auth_field_spacing)))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (it.isNotBlank()) confirmPasswordError = null
                        },
                        label = { Text(stringResource(R.string.confirm_password_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = confirmPasswordError != null,
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (confirmPasswordVisible)
                                            com.example.design.R.drawable.eye_off
                                        else
                                            com.example.design.R.drawable.eye
                                    ),
                                    contentDescription = if (confirmPasswordVisible) stringResource(R.string.password_hide_content_desc) else stringResource(R.string.password_show_content_desc)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (confirmPasswordError != null) {
                        Text(
                            text = stringResource(confirmPasswordError!!),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp,
                            modifier = Modifier.padding(start = dimensionResource(com.example.design.R.dimen.padding_small), top = dimensionResource(com.example.design.R.dimen.padding_small)))
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))

                com.example.design.components.PalatePrimaryButton(
                    textResId = R.string.register_button,
                    onClick = {
                        if (validateFields()) {
                            viewModel.register(name, email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.startup_button_height))
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(dimensionResource(R.dimen.startup_button_radius))
                        ),
                    enabled = registerState !is RegisterUiState.Loading
                )

                Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.have_account),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp
                    )
                    Text(
                        text = stringResource(R.string.login_link),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            navController.navigate(Destination.Login.route) {
                                popUpTo(Destination.Register.route) { inclusive = true }
                            }
                        }
                    )
                }

                if (registerState is RegisterUiState.Loading) {
                    Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))
                    CircularProgressIndicator(
                        modifier = Modifier.width(dimensionResource(R.dimen.auth_vertical_padding)),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (registerState is RegisterUiState.Error) {
                    Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))
                    Text(
                        text = stringResource((registerState as RegisterUiState.Error).messageRes),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}