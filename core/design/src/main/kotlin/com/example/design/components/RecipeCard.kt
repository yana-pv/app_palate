package com.example.design.components

import com.example.design.R
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.design.theme.*

@Composable
fun RecipeCard(
    name: String,
    imageUrl: String,
    category: String,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Card(
        modifier = modifier
            .height(dimensionResource(R.dimen.recipe_card_height))
            .shadow(
                elevation = if (isPressed) dimensionResource(R.dimen.recipe_card_elevation_pressed) else dimensionResource(R.dimen.recipe_card_elevation_default),
                shape = RoundedCornerShape(dimensionResource(R.dimen.recipe_card_corner_radius))
            )
            .border(
                width = dimensionResource(R.dimen.recipe_card_border_width),
                color = if (isPressed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(dimensionResource(R.dimen.recipe_card_corner_radius))
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.recipe_card_corner_radius)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = dimensionResource(R.dimen.padding_small))
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.padding_small))
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.recipe_card_image_corner_radius)))
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_photo),
                    error = painterResource(id = R.drawable.ic_photo)
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(dimensionResource(R.dimen.padding_small))
                        .size(dimensionResource(R.dimen.recipe_card_favorite_btn_size)),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f)
                ) {
                    IconButton(onClick = onSaveClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = stringResource(R.string.save_recipe_content_desc),
                            tint = if (isSaved) FavoriteRed else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(dimensionResource(R.dimen.recipe_card_favorite_icon_size))
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_small), vertical = dimensionResource(R.dimen.padding_extra_small))
            ) {
                Text(
                    text = name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = dimensionResource(R.dimen.text_size_recipe_name).value.sp
                    ),
                    lineHeight = dimensionResource(R.dimen.nav_item_line_height).value.sp
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = dimensionResource(R.dimen.text_size_small).value.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.recipe_card_border_width))
                )
            }
        }
    }
}