package com.example.design.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.PalateColors
import androidx.compose.ui.res.colorResource

@Composable
fun PalatePrimaryButton(
    textResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = PalateColors.PurpleButton,
            contentColor = PalateColors.White,
            disabledContainerColor = colorResource(com.example.design.R.color.gray_light_bg),
            disabledContentColor = colorResource(com.example.design.R.color.gray_text)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        enabled = enabled
    ) {
        Text(
            text = stringResource(textResId),
            fontSize = 14.sp
        )
    }
}