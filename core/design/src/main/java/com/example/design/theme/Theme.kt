package com.example.design.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun PalateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = PalateTypography,
        content = content
    )
}