package com.example.palate.feature.recipe_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.design.R
import com.example.domain.model.Recipe
import com.example.palate.feature.recipe_detail.viewmodel.RecipeDetailViewModel

@Composable
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        when (val state = uiState) {
            is RecipeDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is RecipeDetailUiState.Success -> {
                RecipeDetailContent(
                    recipe = state.recipe,
                    onBackClick = onBackClick
                )
            }
            is RecipeDetailUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun RecipeDetailContent(
    recipe: Recipe,
    onBackClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.padding_extra_large))
    ) {
        item {
            RecipeHeader(
                recipe = recipe,
                onBackClick = onBackClick,
                onWantToCookClick = { /* TODO */ },
                onToListClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        }

        item {
            SectionTitle(stringResource(R.string.recipe_detail_ingredients))
        }

        itemsIndexed(recipe.ingredients) { _, ingredient ->
            IngredientItem(ingredient.name, ingredient.amount)
        }

        item {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            SectionTitle(stringResource(R.string.recipe_detail_preparation))
        }

        itemsIndexed(recipe.instructions) { _, step ->
            PreparationStep(step)
        }
    }
}

@Composable
fun RecipeHeader(
    recipe: Recipe,
    onBackClick: () -> Unit,
    onWantToCookClick: () -> Unit,
    onToListClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.recipe_detail_header_height))
    ) {
        AsyncImage(
            model = recipe.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colorResource(R.color.gradient_start),
                            colorResource(R.color.gradient_end)
                        ),
                        startY = 300f
                    )
                )
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(
                    top = dimensionResource(R.dimen.recipe_detail_back_btn_top_padding),
                    start = dimensionResource(R.dimen.padding_large)
                )
                .size(
                    width = dimensionResource(R.dimen.recipe_detail_back_btn_width),
                    height = dimensionResource(R.dimen.recipe_detail_back_btn_height)
                )
                .background(
                    colorResource(R.color.white_65),
                    RoundedCornerShape(dimensionResource(R.dimen.recipe_detail_back_btn_radius))
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.recipe_detail_back_desc),
                tint = colorResource(R.color.back_button_icon)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(dimensionResource(R.dimen.padding_large))
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = recipe.cuisine,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            
            ActionButtons(
                onWantToCookClick = onWantToCookClick,
                onToListClick = onToListClick
            )
        }
    }
}

@Composable
fun ActionButtons(
    onWantToCookClick: () -> Unit,
    onToListClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        Button(
            onClick = onWantToCookClick,
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.recipe_detail_action_btn_height)),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.secondary_purple)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.recipe_detail_action_btn_radius)),
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.recipe_detail_icon_size))
            )
            Spacer(Modifier.width(dimensionResource(R.dimen.padding_medium)))
            Text(
                text = stringResource(R.string.recipe_detail_want_to_cook),
                fontSize = dimensionResource(R.dimen.text_size_action_button).value.sp
            )
        }
        Button(
            onClick = onToListClick,
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.recipe_detail_action_btn_height)),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_green)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.recipe_detail_action_btn_radius)),
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.shopping_cart),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.recipe_detail_icon_size))
            )
            Spacer(Modifier.width(dimensionResource(R.dimen.padding_medium)))
            Text(
                text = stringResource(R.string.recipe_detail_to_list),
                fontSize = dimensionResource(R.dimen.text_size_action_button).value.sp
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Surface(
        color = colorResource(R.color.section_bg),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_large),
                vertical = dimensionResource(R.dimen.padding_medium)
            )
            .height(dimensionResource(R.dimen.recipe_detail_section_title_height))
            .clip(RoundedCornerShape(dimensionResource(R.dimen.recipe_detail_section_title_radius)))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_large)),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun IngredientItem(name: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_extra_large),
                vertical = dimensionResource(R.dimen.padding_small)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            modifier = Modifier.width(dimensionResource(R.dimen.padding_large))
        )
        Text(
            text = "$name — $amount",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PreparationStep(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_large),
                vertical = dimensionResource(R.dimen.padding_medium)
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
