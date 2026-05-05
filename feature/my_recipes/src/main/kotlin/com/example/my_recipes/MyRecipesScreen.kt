package com.example.my_recipes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.design.components.ConfirmationDialog
import com.example.design.theme.PalateTheme
import com.example.design.theme.PrimaryGreen
import com.example.my_recipes.tabs.CookedTab
import com.example.my_recipes.tabs.MyRecipesTab
import com.example.my_recipes.tabs.WantToCookTab
import com.example.my_recipes.viewModel.MyRecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesScreen(
    viewModel: MyRecipesViewModel = hiltViewModel(),
    onWantToCookClick: (String) -> Unit,
    onCookedNotesClick: (String) -> Unit,
    onMyRecipesClick: (String) -> Unit,
    onMyRecipesEditClick: (String) -> Unit = {},
    onCreateRecipeClick: () -> Unit = {}

) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    val tabs = listOf(
        stringResource(R.string.tab_want_to_cook),
        stringResource(R.string.tab_cooked),
        stringResource(R.string.tab_my_recipes)
    )

    PalateTheme (darkTheme = uiState.isDarkMode) {
        Scaffold(
            containerColor = if (uiState.isDarkMode) Color.Black else MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .background(if (uiState.isDarkMode) Color.Black else MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.my_collection),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = if (uiState.isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = if (uiState.isDarkMode) Color.Black else MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal,
                                            fontSize = 14.sp
                                        ),
                                        color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> WantToCookTab(
                        recipes = uiState.wantToCook,
                        onRecipeClick = onWantToCookClick,
                        onCookedClick = { recipeId -> viewModel.moveToCooked(recipeId) },
                        onUserRecipeClick = onMyRecipesClick,
                        onDeleteClick = { recipeId ->
                            pendingDeleteId = recipeId
                            showDeleteDialog = true
                        }
                    )

                    1 -> CookedTab(
                        recipes = uiState.cooked,
                        onRecipeClick = onWantToCookClick,
                        onUserRecipeClick = onMyRecipesClick,
                        onNotesClick = onCookedNotesClick,
                        onDeleteClick = { recipeId ->
                            pendingDeleteId = recipeId
                            showDeleteDialog = true
                        }
                    )

                    2 -> Box(modifier = Modifier.fillMaxSize()) {
                        MyRecipesTab(
                            recipes = uiState.userRecipes,
                            onRecipeClick = onMyRecipesClick,
                            onEditClick = onMyRecipesEditClick,
                            onDeleteClick = { recipeId ->
                                pendingDeleteId = recipeId
                                showDeleteDialog = true
                            },
                            onCookedClick = { recipe -> viewModel.moveUserRecipeToCooked(recipe) },
                            onAddClick = onCreateRecipeClick
                        )

                        FloatingActionButton(
                            onClick = onCreateRecipeClick,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            containerColor = PrimaryGreen,
                            contentColor = Color.White,
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }


            // Диалог подтверждения удаления
            if (showDeleteDialog && pendingDeleteId != null) {
                ConfirmationDialog(
                    title = stringResource(R.string.delete_confirmation_title),
                    message = stringResource(R.string.delete_confirmation_message),
                    onConfirm = {
                        when (selectedTab) {
                            0 -> viewModel.removeFromWantToCook(pendingDeleteId!!)
                            1 -> viewModel.removeFromCooked(pendingDeleteId!!)
                            2 -> viewModel.deleteUserRecipe(pendingDeleteId!!)
                        }
                        showDeleteDialog = false
                        pendingDeleteId = null
                    },
                    onDismiss = {
                        showDeleteDialog = false
                        pendingDeleteId = null
                    }
                )
            }
        }
    }
}