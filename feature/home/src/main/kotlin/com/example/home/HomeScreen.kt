package com.example.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.design.components.PalateAlertDialog
import com.example.design.components.RecipeCard
import com.example.design.components.RecipeCardPlaceholder
import com.example.domain.model.Category
import com.example.home.viewmodel.HomeViewModel
import com.example.design.theme.*
import com.example.domain.model.Cuisine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRecipeClick: (String) -> Unit = {},
    selectionDate: String? = null,
    selectionMealType: String? = null,
    onSelectRecipe: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    var showSelectionDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                searchQuery = state.searchQuery,
                onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                categories = state.categories,
                selectedCategoryIds = state.selectedCategoryIds,
                onCategorySelected = { viewModel.onCategorySelected(it) },
                onFilterSheetVisible = { viewModel.setFilterSheetVisible(it) },
                isDarkMode = state.isDarkMode
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        HomeContent(
            paddingValues = paddingValues,
            state = state,
            onRecipeClick = { recipeId ->
                if (selectionDate != null && selectionMealType != null) {
                    showSelectionDialog = recipeId
                } else {
                    onRecipeClick(recipeId)
                }
            },
            onSaveClick = { viewModel.toggleSaveRecipe(it) },
            onFilterSheetVisible = { viewModel.setFilterSheetVisible(it) },
            onCuisineSelected = { viewModel.onCuisineSelected(it) },
            onResetFilters = { viewModel.resetFilters() },
            onCategorySelected = { viewModel.onCategorySelected(it) },
            sheetState = sheetState
        )
    }

    if (showSelectionDialog != null) {
        PalateAlertDialog(
            onDismissRequest = { showSelectionDialog = null },
            title = stringResource(com.example.design.R.string.selection_option_title),
            confirmButtonText = stringResource(com.example.design.R.string.cancel),
            onConfirmClick = { showSelectionDialog = null },
            content = {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(com.example.design.R.string.selection_option_view)) },
                        leadingContent = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.clickable {
                            onRecipeClick(showSelectionDialog!!)
                            showSelectionDialog = null
                        }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(com.example.design.R.string.selection_option_select)) },
                        leadingContent = { Icon(Icons.Default.Add, null) },
                        modifier = Modifier.clickable {
                            onSelectRecipe(showSelectionDialog!!)
                            showSelectionDialog = null
                        }
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    categories: List<Category>,
    selectedCategoryIds: Set<String>,
    onCategorySelected: (String) -> Unit,
    onFilterSheetVisible: (Boolean) -> Unit,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(bottom = dimensionResource(com.example.design.R.dimen.padding_small))
    ) {
        // Поиск
        var isFocused by remember { mutableStateOf(false) }
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(com.example.design.R.dimen.padding_large),
                    vertical = dimensionResource(com.example.design.R.dimen.padding_medium)
                )
                .height(dimensionResource(R.dimen.search_bar_height))
                .onFocusChanged { isFocused = it.isFocused }
                .border(
                    width = dimensionResource(com.example.design.R.dimen.recipe_card_border_width),
                    color = if (isDarkMode) {
                        if (isFocused) Color.White else InactiveGray
                    } else {
                        if (isFocused) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    },
                    shape = RoundedCornerShape(dimensionResource(R.dimen.search_bar_corner_radius))
                )
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(dimensionResource(R.dimen.search_bar_corner_radius))
                ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = dimensionResource(com.example.design.R.dimen.text_size_placeholder).value.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensionResource(R.dimen.search_bar_corner_radius)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = if (isDarkMode) {
                            if (isFocused || searchQuery.isNotEmpty()) Color.White else InactiveGray
                        } else {
                            if (isFocused || searchQuery.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant
                        },
                        modifier = Modifier.size(dimensionResource(R.dimen.search_bar_icon_size))
                    )
                    Spacer(Modifier.width(dimensionResource(com.example.design.R.dimen.padding_medium)))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                color = if (isDarkMode) InactiveGray else MaterialTheme.colorScheme.outlineVariant,
                                fontSize = dimensionResource(com.example.design.R.dimen.text_size_placeholder).value.sp
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )

        // Категории
        if (categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(vertical = dimensionResource(com.example.design.R.dimen.padding_small)),
                contentPadding = PaddingValues(horizontal = dimensionResource(com.example.design.R.dimen.padding_large)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_medium))
            ) {
                items(categories.take(9)) { category ->
                    val isSelected = selectedCategoryIds.contains(category.id)
                    val categoryName = if (category.id == "all") {
                        stringResource(R.string.all_categories)
                    } else {
                        category.name
                    }
                    CategoryChip(
                        name = categoryName,
                        isSelected = isSelected,
                        onClick = { onCategorySelected(category.id) },
                        isDarkMode = isDarkMode
                    )
                }
                item {
                    CategoryChip(
                        name = stringResource(R.string.category_more),
                        isSelected = false,
                        onClick = { onFilterSheetVisible(true) },
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    state: HomeUiState,
    onRecipeClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onFilterSheetVisible: (Boolean) -> Unit,
    onCuisineSelected: (String) -> Unit,
    onResetFilters: () -> Unit,
    onCategorySelected: (String) -> Unit,
    sheetState: SheetState
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(com.example.design.R.dimen.padding_large),
                        vertical = dimensionResource(com.example.design.R.dimen.padding_small)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.all_recipes),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { onFilterSheetVisible(true) }) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filters_content_desc)
                    )
                }
            }

            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            val columns = if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                GridCells.Adaptive(minSize = dimensionResource(R.dimen.recipe_card_min_width))
            } else {
                GridCells.Fixed(3)
            }

            LazyVerticalGrid(
                columns = columns,
                contentPadding = PaddingValues(
                    start = dimensionResource(R.dimen.search_bar_corner_radius),
                    end = dimensionResource(R.dimen.search_bar_corner_radius),
                    top = dimensionResource(com.example.design.R.dimen.padding_extra_small),
                    bottom = dimensionResource(com.example.design.R.dimen.nav_bar_height) + dimensionResource(com.example.design.R.dimen.padding_extra_large) * 2
                ),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_extra_small)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_extra_small))
            ) {
                if (state.isLoading && state.recipes.isEmpty()) {
                    items(12) { RecipeCardPlaceholder() }
                } else if (state.recipes.isEmpty()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                        HomeEmptyState(
                            onRetry = { onResetFilters() }
                        )
                    }
                } else {
                    items(state.recipes) { recipe ->
                        RecipeCard(
                            name = recipe.name,
                            imageUrl = recipe.imageUrl,
                            category = recipe.categoryName,
                            isSaved = recipe.isSaved,
                            onSaveClick = { onSaveClick(recipe.id) },
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }

        if (state.isFilterSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { onFilterSheetVisible(false) },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                FilterBottomSheetContent(
                    categories = state.categories,
                    cuisines = state.cuisines,
                    selectedCategoryIds = state.selectedCategoryIds,
                    selectedCuisines = state.selectedCuisines,
                    onCategoryClick = onCategorySelected,
                    onCuisineClick = onCuisineSelected,
                    onApply = { onFilterSheetVisible(false) },
                    onReset = onResetFilters,
                    isDarkMode = state.isDarkMode
                )
            }
        }
    }
}

@Composable
fun HomeEmptyState(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(com.example.design.R.dimen.placeholder_top_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(com.example.design.R.dimen.placeholder_icon_size_large)),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))
        Text(
            text = stringResource(R.string.no_recipes_found),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    categories: List<Category>,
    cuisines: List<Cuisine>,
    selectedCategoryIds: Set<String>,
    selectedCuisines: Set<String>,
    onCategoryClick: (String) -> Unit,
    onCuisineClick: (String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(com.example.design.R.dimen.padding_large))
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(dimensionResource(com.example.design.R.dimen.padding_medium)))
            Text(stringResource(R.string.filters_title), style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

            Text(stringResource(R.string.categories_title), style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.padding(vertical = dimensionResource(com.example.design.R.dimen.padding_medium)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_medium))
            ) {
                CategoryChip(
                    name = stringResource(R.string.all_categories),
                    isSelected = selectedCategoryIds.contains("all"),
                    onClick = { onCategoryClick("all") },
                    isDarkMode = isDarkMode
                )
                categories.filter { it.id != "all" }.forEach { category ->
                    CategoryChip(
                        name = category.name,
                        isSelected = selectedCategoryIds.contains(category.id),
                        onClick = { onCategoryClick(category.id) },
                        isDarkMode = isDarkMode
                    )
                }
            }

            Spacer(Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

            Text(stringResource(R.string.cuisine_title), style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.padding(vertical = dimensionResource(com.example.design.R.dimen.padding_medium)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_medium))
            ) {
                CategoryChip(
                    name = stringResource(R.string.all_cuisines),
                    isSelected = selectedCuisines.isEmpty(),
                    onClick = { onCuisineClick("all") },
                    isDarkMode = isDarkMode
                )
                cuisines.forEach { cuisine ->
                    CategoryChip(
                        name = cuisine.name,
                        isSelected = selectedCuisines.contains(cuisine.originalName),
                        onClick = { onCuisineClick(cuisine.originalName) },
                        isDarkMode = isDarkMode
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(com.example.design.R.dimen.padding_large))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_large))
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.search_bar_corner_radius)),
                    border = BorderStroke(
                        width = dimensionResource(com.example.design.R.dimen.recipe_card_border_width),
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(stringResource(R.string.reset_button))
                }
                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.search_bar_corner_radius)),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.apply_button))
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    Surface(
        modifier = Modifier
            .height(dimensionResource(R.dimen.category_chip_height))
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(R.dimen.category_chip_corner_radius)),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            if (isDarkMode) LightOnSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.category_chip_padding_horizontal)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontSize = dimensionResource(com.example.design.R.dimen.text_size_chip).value.sp
            )
        }
    }
}