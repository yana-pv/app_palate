package com.example.design.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.design.R

val CondimentFont = FontFamily(
    Font(R.font.condiment_regular, FontWeight.Normal)
)

val PalateTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontFamily = CondimentFont,
        fontWeight = FontWeight.W400,
        fontSize = 48.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.1.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.1.sp
    ),

    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    )
)