package com.example.create_recipe.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Название ингредиента
        OutlinedTextField(
            value = ingredient.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.ingredient_name_hint)) },
            modifier = Modifier.weight(2f)
        )

        // Количество
        OutlinedTextField(
            value = ingredient.amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.ingredient_amount_hint)) },
            modifier = Modifier.weight(1f)
        )

        // Единицы измерения (новая колонка)
        OutlinedTextField(
            value = ingredient.unit,
            onValueChange = onUnitChange,
            label = { Text(stringResource(R.string.ingredient_unit_hint)) },
            modifier = Modifier.weight(1f)
        )

        if (showDeleteButton) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.width(48.dp)
            ) {
                Icon(
                    painter = painterResource(com.example.design.R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = ErrorRed
                )
            }
        }
    }
}