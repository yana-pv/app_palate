package com.example.design.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.design.R

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    PalateAlertDialog(
        onDismissRequest = onDismiss,
        title = title,
        text = message,
        confirmButtonText = stringResource(R.string.delete),
        onConfirmClick = onConfirm,
        dismissButtonText = stringResource(R.string.cancel),
        onDismissClick = onDismiss
    )
}