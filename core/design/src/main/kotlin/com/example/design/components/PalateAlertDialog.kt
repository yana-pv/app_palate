package com.example.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PalateAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    dismissButtonText: String? = null,
    onDismissClick: (() -> Unit)? = null,
    title: String? = null,
    text: String? = null,
    content: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = title?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column {
                text?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = if (content != null) 16.dp else 0.dp)
                    )
                }
                content?.invoke()
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmClick,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = dismissButtonText?.let {
            {
                OutlinedButton(
                    onClick = onDismissClick ?: onDismissRequest,
                    shape = RoundedCornerShape(100.dp),
                    border = null
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}
