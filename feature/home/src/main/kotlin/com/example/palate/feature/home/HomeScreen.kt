package com.example.palate.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
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
import com.example.design.R
import com.example.design.components.RecipeCard
import com.example.domain.model.Category
import com.example.palate.feature.home.viewmodel.HomeViewModel
import com.example.theme.CategoryUnselectedBg
import com.example.theme.CategoryUnselectedText
import com.example.theme.SearchBorder
import com.example.theme.SearchPlaceholder
import com.example.theme.SecondaryPurple
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.design.components.BottomNavItem
import com.example.design.components.PalateBottomNav
import com.example.palate.feature.my_recipes.MyRecipesScreen
import com.example.palate.feature.plan.PlanScreen
import com.example.palate.feature.profile.ProfileScreen
import com.example.palate.feature.shopping_list.ShoppingListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRecipeClick: (String) -> Unit = {},
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            PalateBottomNav(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(BottomNavItem.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                val state by viewModel.uiState.collectAsState()
                val sheetState = rememberModalBottomSheetState()

                HomeContent(
                    state = state,
                    onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    onCategorySelected = { viewModel.onCategorySelected(it) },
                    onFilterSheetVisible = { viewModel.setFilterSheetVisible(it) },
                    onRecipeClick = onRecipeClick,
                    onSaveClick = { viewModel.toggleSaveRecipe(it) },
                    onCuisineSelected = { viewModel.onCuisineSelected(it) },
                    onResetFilters = { viewModel.resetFilters() },
                    sheetState = sheetState
                )
            }
            composable(BottomNavItem.Plan.route) { PlanScreen() }
            composable(BottomNavItem.MyRecipes.route) { MyRecipesScreen() }
            composable(BottomNavItem.ShoppingList.route) { ShoppingListScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUiState,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onFilterSheetVisible: (Boolean) -> Unit,
    onRecipeClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onCuisineSelected: (String) -> Unit,
    onResetFilters: () -> Unit,
    sheetState: SheetState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(bottom = dimensionResource(R.dimen.padding_medium))
            ) {
                // Поиск
                var isFocused by remember { mutableStateOf(false) }
                val searchQuery = (state as? HomeUiState.Success)?.searchQuery ?: ""
                
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_large),
                            vertical = dimensionResource(R.dimen.padding_medium)
                        )
                        .height(dimensionResource(R.dimen.search_bar_height))
                        .onFocusChanged { isFocused = it.isFocused }
                        .border(
                            width = dimensionResource(R.dimen.recipe_card_border_width),
                            color = if (isFocused) Color.Black else SearchBorder,
                            shape = RoundedCornerShape(dimensionResource(R.dimen.search_bar_corner_radius))
                        )
                        .background(
                            Color.White,
                            RoundedCornerShape(dimensionResource(R.dimen.search_bar_corner_radius))
                        ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.Black,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(Color.Black),
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
                                tint = if (isFocused || searchQuery.isNotEmpty()) Color.Black else SearchPlaceholder,
                                modifier = Modifier.size(dimensionResource(R.dimen.search_bar_icon_size))
                            )
                            Spacer(Modifier.width(dimensionResource(R.dimen.padding_medium)))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.search_placeholder),
                                        color = SearchPlaceholder,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                )

                // Категории
                if (state is HomeUiState.Success) {
                    val successState = state as HomeUiState.Success
                    LazyRow(
                        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium)),
                        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.padding_large)),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
                    ) {
                        items(successState.categories.take(9)) { category ->
                            val isSelected = successState.selectedCategoryId == category.id
                            val categoryName = if (category.id == "all") {
                                stringResource(R.string.all_categories)
                            } else {
                                category.name
                            }
                            CategoryChip(
                                name = categoryName,
                                isSelected = isSelected,
                                onClick = { onCategorySelected(category.id) }
                            )
                        }
                        item {
                            CategoryChip(
                                name = stringResource(R.string.category_more),
                                isSelected = false,
                                onClick = { onFilterSheetVisible(true) }
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color.White)) {
            when (val uiState = state) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = SecondaryPurple
                    )
                }
                is HomeUiState.Success -> {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = dimensionResource(R.dimen.padding_large),
                                    vertical = dimensionResource(R.dimen.padding_medium)
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

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(
                                horizontal = dimensionResource(R.dimen.search_bar_corner_radius),
                                vertical = dimensionResource(R.dimen.padding_medium)
                            ),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.recipes) { recipe ->
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

                        if (uiState.isFilterSheetVisible) {
                            ModalBottomSheet(
                                onDismissRequest = { onFilterSheetVisible(false) },
                                sheetState = sheetState,
                                containerColor = Color.White
                            ) {
                                FilterBottomSheetContent(
                                    categories = uiState.categories,
                                    cuisines = uiState.cuisines,
                                    selectedCategoryId = uiState.selectedCategoryId,
                                    selectedCuisine = uiState.selectedCuisine,
                                    onCategoryClick = onCategorySelected,
                                    onCuisineClick = onCuisineSelected,
                                    onApply = { onFilterSheetVisible(false) },
                                    onReset = onResetFilters
                                )
                            }
                        }
                    }
                }
                is HomeUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    categories: List<Category>,
    cuisines: List<String>,
    selectedCategoryId: String?,
    selectedCuisine: String?,
    onCategoryClick: (String) -> Unit,
    onCuisineClick: (String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_large))
            .navigationBarsPadding()
    ) {
        Text(stringResource(R.string.filters_title), style = MaterialTheme.typography.headlineSmall)
        
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
        
        Text(stringResource(R.string.categories_title), style = MaterialTheme.typography.titleMedium)
        FlowRow(
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            CategoryChip(
                name = stringResource(R.string.all_categories),
                isSelected = selectedCategoryId == "all" || selectedCategoryId == null,
                onClick = { onCategoryClick("all") }
            )
            categories.filter { it.id != "all" }.forEach { category ->
                CategoryChip(
                    name = category.name,
                    isSelected = selectedCategoryId == category.id,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

        Text(stringResource(R.string.cuisine_title), style = MaterialTheme.typography.titleMedium)
        FlowRow(
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            CategoryChip(
                name = stringResource(R.string.all_categories),
                isSelected = selectedCuisine == null || selectedCuisine == "null",
                onClick = { onCuisineClick("null") }
            )
            cuisines.forEach { cuisine ->
                CategoryChip(
                    name = cuisine,
                    isSelected = selectedCuisine == cuisine,
                    onClick = { onCuisineClick(cuisine) }
                )
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_extra_large)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large))
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(dimensionResource(R.dimen.search_bar_corner_radius))
            ) {
                Text(stringResource(R.string.reset_button))
            }
            Button(
                onClick = onApply,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(dimensionResource(R.dimen.search_bar_corner_radius)),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple)
            ) {
                Text(stringResource(R.string.apply_button))
            }
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(dimensionResource(R.dimen.category_chip_height))
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(R.dimen.category_chip_corner_radius)),
        color = if (isSelected) SecondaryPurple else CategoryUnselectedBg,
        contentColor = if (isSelected) Color.White else CategoryUnselectedText
    ) {
        Box(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.category_chip_padding_horizontal)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontSize = 12.sp
            )
        }
    }
}