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
import com.example.domain.model.CookedRecipe
import com.example.my_recipes.R
import com.example.my_recipes.components.CookedRecipeCard

@Composable
fun CookedTab(
    recipes: List<CookedRecipe>,
    onRecipeClick: (String) -> Unit,
    onNotesClick: (String) -> Unit,
    onUserRecipeClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (recipes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.empty_cooked))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.my_recipes_content_padding))
        ) {
            items(recipes) { recipe ->
                CookedRecipeCard(
                    name = recipe.name,
                    imageUrl = recipe.imageUrl,
                    category = recipe.category,
                    rating = recipe.userRating,
                    onNotesClick = { onNotesClick(recipe.recipeId) },
                    onDeleteClick = { onDeleteClick(recipe.recipeId) },
                    onClick = {
                        val isUserRecipe = !recipe.recipeId.matches(Regex("\\d+"))
                        if (isUserRecipe) {
                            onUserRecipeClick(recipe.recipeId)
                        } else {
                            onRecipeClick(recipe.recipeId)
                        }
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
                )
            }
        }
    }
}