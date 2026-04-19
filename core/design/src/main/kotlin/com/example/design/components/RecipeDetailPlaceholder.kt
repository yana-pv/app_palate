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
fun RecipeDetailPlaceholder() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header image placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.placeholder_detail_image_height))
                .shimmerEffect()
        )
        
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_extra_large)))
        
        // Title placeholder
        Box(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.padding_extra_large))
                .fillMaxWidth(0.7f)
                .height(dimensionResource(R.dimen.placeholder_detail_title_height))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_medium)))
                .shimmerEffect()
        )
        
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
        
        // Buttons placeholder
        Row(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.padding_extra_large))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large))
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensionResource(com.example.design.R.dimen.nav_item_indicator_width))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_large)))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensionResource(com.example.design.R.dimen.nav_item_indicator_width))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_large)))
                    .shimmerEffect()
            )
        }
        
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.placeholder_detail_title_height)))
        
        // Section title
        Box(
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.padding_extra_large))
                .fillMaxWidth(0.4f)
                .height(dimensionResource(R.dimen.placeholder_detail_section_height))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_medium)))
                .shimmerEffect()
        )
        
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
        
        // Content items
        repeat(5) {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_extra_large),
                        vertical = dimensionResource(R.dimen.padding_medium)
                    )
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.placeholder_detail_item_height))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_small)))
                    .shimmerEffect()
            )
        }
    }
}
