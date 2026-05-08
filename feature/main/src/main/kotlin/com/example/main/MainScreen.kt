package com.example.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.MealType
import com.example.plan.PlanViewModel
import com.example.design.components.BottomNavItemData
import com.example.design.components.PalateBottomNav
import com.example.home.HomeScreen
import com.example.navigation.Destination
import com.example.plan.PlanScreen
import com.example.my_recipes.MyRecipesScreen
import com.example.shopping_list.ShoppingListScreen
import com.example.profile.ui.ProfileScreen
import java.time.LocalDate
import com.example.design.R as DesignR

@Composable
fun MainScreen(
    onRecipeClick: (String, String?, String?) -> Unit,
    onCookedNoteClick: (String) -> Unit,
    onCreateRecipeClick: () -> Unit,
    onMyRecipesClick: (String, String?, String?) -> Unit,
    onEditRecipeClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val planViewModel: PlanViewModel = hiltViewModel()

    val bottomNavItems = listOf(
        BottomNavItemData(
            route = Destination.Plan.route,
            titleRes = DesignR.string.nav_plan,
            icon = ImageVector.vectorResource(DesignR.drawable.calendar)
        ),
        BottomNavItemData(
            route = Destination.MyRecipes.createRoute(),
            titleRes = DesignR.string.nav_my_recipes,
            icon = ImageVector.vectorResource(DesignR.drawable.book_open)
        ),
        BottomNavItemData(
            route = Destination.Home.createRoute(),
            titleRes = DesignR.string.nav_home,
            icon = Icons.Default.Search
        ),
        BottomNavItemData(
            route = Destination.ShoppingList.route,
            titleRes = DesignR.string.nav_shopping_list,
            icon = ImageVector.vectorResource(DesignR.drawable.shopping_cart)
        ),
        BottomNavItemData(
            route = Destination.Profile.route,
            titleRes = DesignR.string.nav_profile,
            icon = ImageVector.vectorResource(DesignR.drawable.user)
        )
    )

    Scaffold(
        bottomBar = {
            PalateBottomNav(
                items = bottomNavItems,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Destination.Home.route) { backStackEntry ->
                val rawDate = backStackEntry.arguments?.getString("date")
                val rawMealType = backStackEntry.arguments?.getString("mealType")

                val date = if (rawDate.isNullOrBlank() || rawDate.contains("{") || rawDate == "null") null else rawDate
                val mealType = if (rawMealType.isNullOrBlank() || rawMealType.contains("{") || rawMealType == "null") null else rawMealType

                val recipeSelected by backStackEntry.savedStateHandle.getStateFlow("recipe_selected", false).collectAsState()

                LaunchedEffect(recipeSelected) {
                    if (recipeSelected) {
                        navController.navigate(Destination.Plan.route) {
                            popUpTo(Destination.Home.route) { inclusive = false }
                        }
                        backStackEntry.savedStateHandle["recipe_selected"] = false
                    }
                }

                HomeScreen(
                    onRecipeClick = { id -> onRecipeClick(id, date, mealType) },
                    selectionDate = date,
                    selectionMealType = mealType,
                    onSelectRecipe = { recipeId ->
                        if (date != null && mealType != null) {
                            planViewModel.addMeal(
                                date = LocalDate.parse(date),
                                mealType = MealType.valueOf(mealType),
                                recipeId = recipeId,
                                isUserRecipe = false
                            )
                            navController.navigate(Destination.Plan.route) {
                                popUpTo(Destination.Home.route) { inclusive = false }
                            }
                        }
                    }
                )
            }
            composable(Destination.Plan.route) {
                PlanScreen(
                    viewModel = planViewModel,
                    onNavigateToRecipe = { id, isUserRecipe ->
                        if (isUserRecipe) {
                            onMyRecipesClick(id, null, null)
                        } else {
                            onRecipeClick(id, null, null)
                        }
                    },
                    onNavigateToMyRecipes = { date, mealType ->
                        navController.navigate(Destination.MyRecipes.createRoute(date, mealType))
                    },
                    onNavigateToSearch = { date, mealType ->
                        navController.navigate(Destination.Home.createRoute(date, mealType))
                    }
                )
            }
            composable(Destination.MyRecipes.route) { backStackEntry ->
                val rawDate = backStackEntry.arguments?.getString("date")
                val rawMealType = backStackEntry.arguments?.getString("mealType")
                
                val date = if (rawDate.isNullOrBlank() || rawDate.contains("{") || rawDate == "null") null else rawDate
                val mealType = if (rawMealType.isNullOrBlank() || rawMealType.contains("{") || rawMealType == "null") null else rawMealType

                MyRecipesScreen(
                    onWantToCookClick = { id -> onRecipeClick(id, date, mealType) },
                    onCookedNotesClick = onCookedNoteClick,
                    onMyRecipesClick = { id -> onMyRecipesClick(id, date, mealType) },
                    onMyRecipesEditClick = onEditRecipeClick,
                    onCreateRecipeClick = onCreateRecipeClick,
                    selectionDate = date,
                    selectionMealType = mealType,
                    onSelectRecipe = { recipeId, isUserRecipe ->
                        if (date != null && mealType != null) {
                            planViewModel.addMeal(
                                date = LocalDate.parse(date),
                                mealType = MealType.valueOf(mealType),
                                recipeId = recipeId,
                                isUserRecipe = isUserRecipe
                            )
                            navController.navigate(Destination.Plan.route) {
                                popUpTo(Destination.Home.route) { inclusive = false }
                            }
                        }
                    }
                )
            }

            composable(Destination.ShoppingList.route) { 
                ShoppingListScreen()
            }
            composable(Destination.Profile.route) { 
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
