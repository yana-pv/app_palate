package com.example.create_recipe.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.create_recipe.R
import com.example.design.theme.ErrorRed

@Composable
fun IngredientInputRow(
    ingredient: com.example.domain.model.Ingredient,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDeleteButton: Boolean = true
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Название ингредиента
        OutlinedTextField(
            value = ingredient.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.ingredient_name_hint), maxLines = 1) },
            modifier = Modifier.weight(1.5f),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true
        )

        // Количество
        OutlinedTextField(
            value = ingredient.amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.ingredient_amount_hint), maxLines = 1) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true
        )

        // Единицы измерения 
        OutlinedTextField(
            value = ingredient.unit,
            onValueChange = onUnitChange,
            label = { Text(stringResource(R.string.ingredient_unit_hint), maxLines = 1) },
            modifier = Modifier.weight(0.8f),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true
        )

        if (showDeleteButton) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(com.example.design.R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = ErrorRed
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}
