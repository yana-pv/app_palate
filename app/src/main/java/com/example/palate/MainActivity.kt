package com.example.palate

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.ui.AuthViewModel
import com.example.auth.ui.LoginScreen
import com.example.auth.ui.RegisterScreen
import com.example.auth.ui.StartupScreen
import com.example.navigation.Destination
import com.example.home.HomeScreen
import com.example.palate.navigation.PalateNavigator
import com.example.recipe_detail.RecipeDetailScreen
import com.example.palate.ui.theme.PalateTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        setContent {
            PalateTheme {
                val navController = rememberNavController()
                val navigator = remember(navController) { PalateNavigator(navController) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "startup",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("startup") {
                            StartupScreen(navController)
                        }
                        composable("login") {
                            LoginScreen(navController, authViewModel)
                        }
                        composable("register") {
                            RegisterScreen(navController, authViewModel)
                        }

                        composable(Destination.Home.route) {
                            HomeScreen(
                                onRecipeClick = { recipeId ->
                                    navigator.openRecipeDetail(recipeId)
                                }
                            )
                        }

                        composable(Destination.RecipeDetail.route) {
                            RecipeDetailScreen(
                                onBackClick = { navigator.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}