package com.example.my_recipes.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.domain.model.UserRecipe
import com.example.my_recipes.R
import com.example.my_recipes.components.MyRecipesCard

@Composable
fun MyRecipesTab(
    recipes: List<UserRecipe>,
    onRecipeClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onCookedClick: (UserRecipe) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (recipes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.empty_my_recipes))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = dimensionResource(R.dimen.my_recipes_content_padding),
                    start = dimensionResource(R.dimen.my_recipes_content_padding),
                    end = dimensionResource(R.dimen.my_recipes_content_padding),
                    bottom = dimensionResource(R.dimen.my_recipes_bottom_padding)
                )
            ) {
                items(recipes) { recipe ->
                    MyRecipesCard(
                        name = recipe.name,
                        imageUrl = recipe.imagePath ?: "",
                        category = recipe.category,
                        onEditClick = { onEditClick(recipe.id) },
                        onDeleteClick = { onDeleteClick(recipe.id) },
                        onClick = { onRecipeClick(recipe.id) },
                        onCookedClick = { onCookedClick(recipe) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }

    }
}