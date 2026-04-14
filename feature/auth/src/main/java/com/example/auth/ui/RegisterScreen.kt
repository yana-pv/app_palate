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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.auth.R
import com.example.design.theme.CondimentFont
import com.example.design.theme.PalateColors

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val registerState by viewModel.registerState.collectAsState()

    val errorNameRequired = stringResource(R.string.error_name_required)
    val errorEmailRequired = stringResource(R.string.error_email_required)
    val errorEmailInvalid = stringResource(R.string.error_email_invalid)
    val errorPasswordRequired = stringResource(R.string.error_password_required)
    val errorPasswordMin = stringResource(R.string.error_password_min)
    val errorConfirmRequired = stringResource(R.string.error_confirm_password_required)
    val errorPasswordMismatch = stringResource(R.string.error_password_mismatch)

    fun validateFields(): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameError = errorNameRequired
            isValid = false
        } else {
            nameError = null
        }

        if (email.isBlank()) {
            emailError = errorEmailRequired
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = errorEmailInvalid
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = errorPasswordRequired
            isValid = false
        } else if (password.length < 6) {
            passwordError = errorPasswordMin
            isValid = false
        } else {
            passwordError = null
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = errorConfirmRequired
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = errorPasswordMismatch
            isValid = false
        } else {
            confirmPasswordError = null
        }

        return isValid
    }

    if (registerState is RegisterUiState.Success) {
        LaunchedEffect(Unit) {
            navController.navigate("home") {
                popUpTo("startup") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.startup_background),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(650.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(PalateColors.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontFamily = CondimentFont,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Normal,
                    color = PalateColors.GreenPrimary,
                    letterSpacing = 0.1.sp,
                    lineHeight = 25.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = PalateColors.GrayBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(16.dp)
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
                    if (nameError != null) {
                        Text(text = nameError!!, color = PalateColors.ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                    if (emailError != null) {
                        Text(text = emailError!!, color = PalateColors.ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (passwordVisible)
                                            android.R.drawable.ic_menu_close_clear_cancel
                                        else
                                            android.R.drawable.ic_menu_view
                                    ),
                                    contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                                )
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
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = PalateColors.ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                    } else {
                        Text(
                            text = stringResource(R.string.password_min_hint),
                            fontSize = 12.sp,
                            color = PalateColors.GrayText,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (confirmPasswordVisible)
                                            android.R.drawable.ic_menu_close_clear_cancel
                                        else
                                            android.R.drawable.ic_menu_view
                                    ),
                                    contentDescription = if (confirmPasswordVisible) "Скрыть пароль" else "Показать пароль"
                                )
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
                    if (confirmPasswordError != null) {
                        Text(text = confirmPasswordError!!, color = PalateColors.ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (validateFields()) {
                            viewModel.register(name, email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PalateColors.PurpleButton
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = registerState !is RegisterUiState.Loading
                ) {
                    Text(
                        text = stringResource(R.string.register_button),
                        color = PalateColors.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.have_account),
                        color = PalateColors.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(R.string.login_link),
                        color = PalateColors.PurpleButton,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }

                if (registerState is RegisterUiState.Loading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.width(32.dp),
                        color = PalateColors.PurpleButton
                    )
                }

                if (registerState is RegisterUiState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (registerState as RegisterUiState.Error).message,
                        color = PalateColors.ErrorRed,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}