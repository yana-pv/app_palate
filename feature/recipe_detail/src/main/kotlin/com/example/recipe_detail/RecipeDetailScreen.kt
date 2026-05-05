package com.example.recipe_detail

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.design.theme.*
import com.example.design.R as DesignR
import com.example.design.components.RecipeDetailPlaceholder
import com.example.domain.model.Recipe
import com.example.recipe_detail.viewmodel.RecipeDetailViewModel

@Composable
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading && uiState.recipe == null) {
                RecipeDetailPlaceholder()
            } else {
                uiState.recipe?.let { recipe ->
                    RecipeDetailContent(
                        recipe = recipe,
                        onBackClick = onBackClick,
                        onWantToCookClick = { viewModel.addToWantToCook(recipe.id) },
                        onToListClick = { viewModel.addToShoppingList(recipe.id) }
                    )
                } ?: RecipeErrorState(
                    onBackClick = onBackClick,
                    onRetryClick = { viewModel.loadRecipe() }
                )
            }
        }
    }
}

@Composable
fun RecipeErrorState(
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(DesignR.dimen.padding_large)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = com.example.design.R.drawable.icon),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(com.example.design.R.dimen.placeholder_icon_size_extra_large)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))
        Text(
            text = stringResource(R.string.recipe_not_found),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))
        Text(
            text = stringResource(R.string.error_loading_recipe),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_extra_large)))
        Button(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text(text = stringResource(R.string.retry))
        }
        Spacer(modifier = Modifier.height(dimensionResource(DesignR.dimen.padding_medium)))
        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.back_to_home))
        }
    }
}

@Composable
fun RecipeDetailContent(
    recipe: Recipe,
    onBackClick: () -> Unit,
    onWantToCookClick: () -> Unit,
    onToListClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = dimensionResource(DesignR.dimen.padding_extra_large))
    ) {
        item {
            RecipeHeader(
                recipe = recipe,
                onBackClick = onBackClick,
                onWantToCookClick = onWantToCookClick,
                onToListClick = onToListClick
            )
            Spacer(modifier = Modifier.height(dimensionResource(DesignR.dimen.padding_medium)))
        }

        item {
            SectionTitle(stringResource(R.string.recipe_detail_ingredients))
        }

        itemsIndexed(recipe.ingredients) { _, ingredient ->
            IngredientItem(ingredient.name, ingredient.amount)
        }

        item {
            Spacer(modifier = Modifier.height(dimensionResource(DesignR.dimen.padding_large)))
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
            .heightIn(min = dimensionResource(R.dimen.recipe_detail_header_height))
    ) {
        AsyncImage(
            model = recipe.imageUrl,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            GradientEnd.copy(alpha = 0.5f),
                            GradientEnd
                        ),
                        startY = 0f
                    )
                )
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    top = dimensionResource(DesignR.dimen.padding_medium),
                    start = dimensionResource(DesignR.dimen.padding_large)
                )
                .size(
                    width = dimensionResource(R.dimen.recipe_detail_back_btn_width),
                    height = dimensionResource(R.dimen.recipe_detail_back_btn_height)
                )
                .background(
                    LightWhite65,
                    RoundedCornerShape(dimensionResource(R.dimen.recipe_detail_back_btn_radius))
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.recipe_detail_back_desc),
                tint = LightBackButtonIcon
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(dimensionResource(DesignR.dimen.padding_large))
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                softWrap = true,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recipe.cuisine,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(dimensionResource(DesignR.dimen.padding_large)))

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
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(DesignR.dimen.padding_medium))
    ) {
        Button(
            onClick = onWantToCookClick,
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.recipe_detail_action_btn_height)),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(dimensionResource(R.dimen.recipe_detail_action_btn_radius)),
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            Icon(
                painter = painterResource(id = com.example.design.R.drawable.icon),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.recipe_detail_icon_size))
            )
            Spacer(Modifier.width(dimensionResource(com.example.design.R.dimen.padding_medium)))
            Text(
                text = stringResource(R.string.recipe_detail_want_to_cook),
                fontSize = dimensionResource(com.example.design.R.dimen.text_size_action_button).value.sp
            )
        }
        Button(
            onClick = onToListClick,
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.recipe_detail_action_btn_height)),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = RoundedCornerShape(dimensionResource(R.dimen.recipe_detail_action_btn_radius)),
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            Icon(
                painter = painterResource(id = com.example.design.R.drawable.shopping_cart),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.recipe_detail_icon_size))
            )
            Spacer(Modifier.width(dimensionResource(com.example.design.R.dimen.padding_medium)))
            Text(
                text = stringResource(R.string.recipe_detail_to_list),
                fontSize = dimensionResource(com.example.design.R.dimen.text_size_action_button).value.sp
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(com.example.design.R.dimen.padding_large),
                vertical = dimensionResource(com.example.design.R.dimen.padding_medium)
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
                modifier = Modifier.padding(horizontal = dimensionResource(com.example.design.R.dimen.padding_large)),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                horizontal = dimensionResource(DesignR.dimen.padding_extra_large),
                vertical = dimensionResource(DesignR.dimen.padding_small)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            modifier = Modifier.width(dimensionResource(DesignR.dimen.padding_large)),
            fontSize = dimensionResource(DesignR.dimen.text_size_normal).value.sp
        )
        Text(
            text = "$name — $amount",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PreparationStep(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(DesignR.dimen.padding_large),
                vertical = dimensionResource(DesignR.dimen.padding_medium)
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = dimensionResource(DesignR.dimen.text_size_normal).value.sp
        )
    }
}