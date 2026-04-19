package com.example.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.example.design.components.BottomNavItemData
import com.example.design.components.PalateBottomNav
import com.example.home.HomeScreen
import com.example.navigation.Destination
import com.example.plan.PlanScreen
import com.example.my_recipes.MyRecipesScreen
import com.example.shopping_list.ShoppingListScreen
import com.example.profile.ProfileScreen
import com.example.design.R as DesignR

@Composable
fun MainScreen(
    onRecipeClick: (String) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItemData(
            route = Destination.Plan.route,
            titleRes = DesignR.string.nav_plan,
            icon = ImageVector.vectorResource(DesignR.drawable.calendar)
        ),
        BottomNavItemData(
            route = Destination.MyRecipes.route,
            titleRes = DesignR.string.nav_my_recipes,
            icon = ImageVector.vectorResource(DesignR.drawable.book_open)
        ),
        BottomNavItemData(
            route = Destination.Home.route,
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
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier
        ) {
            composable(Destination.Home.route) {
                HomeScreen(onRecipeClick = onRecipeClick)
            }
            composable(Destination.Plan.route) { PlanScreen() }
            composable(Destination.MyRecipes.route) { MyRecipesScreen() }
            composable(Destination.ShoppingList.route) { ShoppingListScreen() }
            composable(Destination.Profile.route) { ProfileScreen() }
        }
    }
}
