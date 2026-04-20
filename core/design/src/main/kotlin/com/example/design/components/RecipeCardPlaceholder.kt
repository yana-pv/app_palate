package com.example.design.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import com.example.design.R
import com.example.design.utils.shimmerEffect

@Composable
fun RecipeCardPlaceholder() {
    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .width(dimensionResource(R.dimen.recipe_card_image_size))
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.recipe_card_image_size))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.recipe_card_image_corner_radius)))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.placeholder_shimmer_height_large))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_small)))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
        Box(
            modifier = Modifier
                .width(dimensionResource(R.dimen.placeholder_shimmer_width_small))
                .height(dimensionResource(R.dimen.placeholder_shimmer_height_small))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_small)))
                .shimmerEffect()
        )
    }
}
