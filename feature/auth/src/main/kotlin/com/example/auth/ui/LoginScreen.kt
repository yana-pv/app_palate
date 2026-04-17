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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
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
import androidx.navigation.NavController
import com.example.auth.R
import com.example.design.theme.CondimentFont

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val loginState by viewModel.loginState.collectAsState()

    val errorEmailRequired = stringResource(R.string.error_email_required)
    val errorEmailInvalid = stringResource(R.string.error_email_invalid)
    val errorPasswordRequired = stringResource(R.string.error_password_required)
    val errorPasswordMin = stringResource(R.string.error_password_min)

    fun validateFields(): Boolean {
        var isValid = true

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

        return isValid
    }

    if (loginState is LoginUiState.Success) {
        LaunchedEffect(Unit) {
            navController.navigate("home") {
                popUpTo("startup") { inclusive = true }
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
                .background(colorResource(com.example.design.R.color.white))
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
                    color = colorResource(com.example.design.R.color.primary_green),
                    letterSpacing = dimensionResource(R.dimen.auth_logo_letter_spacing).value.sp,
                    lineHeight = dimensionResource(R.dimen.auth_logo_line_height).value.sp
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.auth_vertical_padding)))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = dimensionResource(R.dimen.auth_field_border_width),
                            color = colorResource(com.example.design.R.color.gray_light_bg),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(dimensionResource(R.dimen.auth_field_padding))
                ) {

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
                            focusedBorderColor = colorResource(com.example.design.R.color.gray_light_bg),
                            unfocusedBorderColor = colorResource(com.example.design.R.color.gray_light_bg),
                            focusedLabelColor = colorResource(com.example.design.R.color.primary_green),
                            unfocusedLabelColor = colorResource(com.example.design.R.color.gray_text),
                            focusedTextColor = colorResource(com.example.design.R.color.black),
                            unfocusedTextColor = colorResource(com.example.design.R.color.black),
                            cursorColor = colorResource(com.example.design.R.color.primary_green)
                        )
                    )
                    if (emailError != null) {
                        Text(text = emailError!!, color = colorResource(com.example.design.R.color.error_red), fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp, modifier = Modifier.padding(start = dimensionResource(com.example.design.R.dimen.padding_small), top = dimensionResource(com.example.design.R.dimen.padding_small)))
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
                            focusedBorderColor = colorResource(com.example.design.R.color.gray_light_bg),
                            unfocusedBorderColor = colorResource(com.example.design.R.color.gray_light_bg),
                            focusedLabelColor = colorResource(com.example.design.R.color.primary_green),
                            unfocusedLabelColor = colorResource(com.example.design.R.color.gray_text),
                            focusedTextColor = colorResource(com.example.design.R.color.black),
                            unfocusedTextColor = colorResource(com.example.design.R.color.black),
                            cursorColor = colorResource(com.example.design.R.color.primary_green)
                        )
                    )
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = colorResource(com.example.design.R.color.error_red), fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp, modifier = Modifier.padding(start = dimensionResource(com.example.design.R.dimen.padding_small), top = dimensionResource(com.example.design.R.dimen.padding_small)))
                    } else {
                        Text(
                            text = stringResource(R.string.password_min_hint),
                            fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp,
                            color = colorResource(com.example.design.R.color.gray_text),
                            modifier = Modifier.padding(start = dimensionResource(com.example.design.R.dimen.padding_small), top = dimensionResource(com.example.design.R.dimen.padding_small))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.auth_vertical_padding)))

                Button(
                    onClick = {
                        if (validateFields()) {
                            viewModel.login(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.startup_button_height))
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(dimensionResource(R.dimen.startup_button_radius))),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(com.example.design.R.color.primary_purple)
                    ),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.startup_button_radius)),
                    enabled = loginState !is LoginUiState.Loading
                ) {
                    Text(
                        text = stringResource(R.string.login_button),
                        color = colorResource(com.example.design.R.color.white),
                        fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_account),
                        color = colorResource(com.example.design.R.color.black),
                        fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp
                    )
                    Text(
                        text = stringResource(R.string.register_link),
                        color = colorResource(com.example.design.R.color.primary_purple),
                        fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            navController.navigate("register") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                if (loginState is LoginUiState.Loading) {
                    Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))
                    CircularProgressIndicator(
                        modifier = Modifier.width(dimensionResource(R.dimen.auth_vertical_padding)),
                        color = colorResource(com.example.design.R.color.primary_purple)
                    )
                }

                if (loginState is LoginUiState.Error) {
                    Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))
                    Text(
                        text = (loginState as LoginUiState.Error).message,
                        color = colorResource(com.example.design.R.color.error_red),
                        fontSize = dimensionResource(R.dimen.auth_error_text_size).value.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}