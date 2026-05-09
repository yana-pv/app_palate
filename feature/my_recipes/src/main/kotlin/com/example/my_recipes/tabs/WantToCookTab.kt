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
import com.example.domain.model.Recipe
import com.example.my_recipes.R
import com.example.my_recipes.components.WantToCookCard

@Composable
fun WantToCookTab(
    recipes: List<Recipe>,
    onRecipeClick: (String) -> Unit,
    onUserRecipeClick: (String) -> Unit,
    onCookedClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (recipes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.empty_want_to_cook))
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
                val isUserRecipe = !recipe.id.matches(Regex("\\d+"))
                WantToCookCard(
                    id = recipe.id,
                    name = recipe.name,
                    imageUrl = recipe.imageUrl,
                    category = recipe.category,
                    isCooked = false,
                    onCookedClick = { onCookedClick(recipe.id) },
                    onDeleteClick = { onDeleteClick(recipe.id) },
                    onClick = {
                        if (isUserRecipe) {
                            onUserRecipeClick(recipe.id)
                        } else {
                            onRecipeClick(recipe.id)
                        }
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(com.example.design.R.dimen.padding_small))
                )
            }
        }
    }
}
