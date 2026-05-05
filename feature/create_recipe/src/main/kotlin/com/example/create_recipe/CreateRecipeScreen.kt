package com.example.create_recipe

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.create_recipe.components.IngredientInputRow
import com.example.design.theme.PalateColors
import com.example.design.theme.PalateTheme
import com.example.design.R as DesignR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    recipeId: String? = null,
    viewModel: CreateRecipeViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateImageUri(it.toString()) }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaved()
        }
    }

    PalateTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.create_recipe_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = { viewModel.saveRecipe(context.contentResolver) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PalateColors.GreenPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Блок добавления фото
                item {
                    PhotoUploadSection(
                        imageUri = uiState.imageUri,
                        onAddPhotoClick = { imagePickerLauncher.launch("image/*") }
                    )
                }

                // Название рецепта
                item {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        label = { Text(stringResource(R.string.recipe_name_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Кухня
                item {
                    OutlinedTextField(
                        value = uiState.cuisine,
                        onValueChange = viewModel::updateCuisine,
                        label = { Text(stringResource(R.string.cuisine_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Категория
                item {
                    OutlinedTextField(
                        value = uiState.category,
                        onValueChange = viewModel::updateCategory,
                        label = { Text(stringResource(R.string.category_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Ингредиенты
                item {
                    SectionTitle(stringResource(R.string.ingredients_title))
                }

                itemsIndexed(uiState.ingredients) { index, ingredient ->
                    IngredientInputRow(
                        ingredient = ingredient,
                        onNameChange = { name -> viewModel.updateIngredient(index, name, ingredient.amount, ingredient.unit) },
                        onAmountChange = { amount -> viewModel.updateIngredient(index, ingredient.name, amount, ingredient.unit) },
                        onUnitChange = { unit -> viewModel.updateIngredient(index, ingredient.name, ingredient.amount, unit) },
                        onDeleteClick = { viewModel.removeIngredient(index) },
                        showDeleteButton = uiState.ingredients.size > 1,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Кнопка добавления ингредиента
                item {
                    Button(
                        onClick = { viewModel.addIngredient() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PalateColors.GreenPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.add_ingredient),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                // Приготовление
                item {
                    SectionTitle(stringResource(R.string.preparation_title))
                }

                item {
                    OutlinedTextField(
                        value = uiState.instructions,
                        onValueChange = viewModel::updateInstructions,
                        placeholder = { Text(stringResource(R.string.preparation_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 8,
                        maxLines = 15
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}


@Composable
fun PhotoUploadSection(
    imageUri: String?,
    onAddPhotoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .clip(RoundedCornerShape(12.dp))
            .drawWithCache {
                val strokeWidth = 2.dp.toPx()
                val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                onDrawBehind {
                    drawRoundRect(
                        color = Color(0xFFB3B3B3),
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = dashPathEffect
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                    )
                }
            }
            .clickable { onAddPhotoClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(DesignR.drawable.ic_photo),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFB3B3B3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.add_photo),
                    color = Color(0xFFB3B3B3),
                    fontSize = 14.sp
                )
            }
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
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
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
