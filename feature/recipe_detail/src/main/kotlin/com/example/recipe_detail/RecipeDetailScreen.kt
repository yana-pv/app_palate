package com.example.recipe_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.example.design.components.PalateAlertDialog
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

    val successMessage = stringResource(R.string.ingredients_added)
    val genericErrorMessage = stringResource(R.string.error_failed_to_add)

    LaunchedEffect(uiState.isSelected) {
        if (uiState.isSelected) {
            onBackClick()
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(genericErrorMessage)
            viewModel.clearError()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(successMessage)
            viewModel.clearSuccess()
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
                        isSelectionMode = uiState.isSelectionMode,
                        onBackClick = onBackClick,
                        onToListClick = { viewModel.addIngredientsToShoppingList() },
                        onWantToCookClick = { viewModel.addToWantToCook() },
                        onSelectClick = { viewModel.selectRecipe() }
                    )
                } ?: RecipeErrorState(
                    onBackClick = onBackClick,
                    onRetryClick = { viewModel.loadRecipe() }
                )
            }
        }
    }

    if (uiState.showAlreadyAddedDialog) {
        PalateAlertDialog(
            onDismissRequest = { viewModel.dismissAlreadyAddedDialog() },
            title = stringResource(R.string.already_added_title),
            text = stringResource(R.string.already_added_message),
            confirmButtonText = stringResource(R.string.add_more),
            onConfirmClick = {
                viewModel.addIngredientsToShoppingList(force = true)
                viewModel.dismissAlreadyAddedDialog()
            },
            dismissButtonText = stringResource(R.string.cancel),
            onDismissClick = { viewModel.dismissAlreadyAddedDialog() }
        )
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
    isSelectionMode: Boolean,
    onBackClick: () -> Unit,
    onWantToCookClick: () -> Unit,
    onToListClick: () -> Unit,
    onSelectClick: () -> Unit
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
                isSelectionMode = isSelectionMode,
                onBackClick = onBackClick,
                onWantToCookClick = onWantToCookClick,
                onToListClick = onToListClick,
                onSelectClick = onSelectClick
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
    isSelectionMode: Boolean,
    onBackClick: () -> Unit,
    onWantToCookClick: () -> Unit,
    onToListClick: () -> Unit,
    onSelectClick: () -> Unit
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
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = DesignR.drawable.ic_photo),
            error = painterResource(id = DesignR.drawable.ic_photo)
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

            if (isSelectionMode) {
                Button(
                    onClick = onSelectClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.recipe_detail_action_btn_height)),
                    colors = ButtonDefaults.buttonColors(containerColor = PalateColors.GreenPrimary),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.recipe_detail_action_btn_radius))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.plan_select_now),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                ActionButtons(
                    onWantToCookClick = onWantToCookClick,
                    onToListClick = onToListClick
                )
            }
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