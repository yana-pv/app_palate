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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.design.theme.*
import com.example.domain.model.Recipe
import com.example.recipe_detail.viewmodel.CookedNoteViewModel

@Composable
fun CookedNoteScreen(
    viewModel: CookedNoteViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var rating by rememberSaveable { mutableIntStateOf(0) }
    var noteText by rememberSaveable { mutableStateOf("") }


    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            rating = uiState.initialRating
            noteText = uiState.initialNote
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val recipe = uiState.recipe
    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Рецепт не найден")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            CookedNoteHeader(
                recipe = recipe,
                onBackClick = onBackClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            RatingSection(
                rating = rating,
                onRatingChange = { rating = it }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(stringResource(R.string.recipe_detail_ingredients))
        }

        itemsIndexed(recipe.ingredients) { _, ingredient ->
            IngredientItem(ingredient.name, ingredient.amount)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(stringResource(R.string.recipe_detail_preparation))
        }

        itemsIndexed(recipe.instructions) { _, step ->
            PreparationStep(step)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(stringResource(R.string.my_notes_section))
        }

        item {
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text(stringResource(R.string.note_placeholder)) },
                minLines = 5,
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PalateColors.GreenPrimary,
                    focusedLabelColor = PalateColors.GreenPrimary
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.saveNote(rating, noteText)
                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PalateColors.GreenPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_button),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
@Composable
fun RatingSection(
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.your_rating),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) { index ->
                val starRating = index + 1
                IconButton(
                    onClick = { onRatingChange(starRating) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (starRating <= rating) {
                                android.R.drawable.btn_star_big_on
                            } else {
                                android.R.drawable.btn_star_big_off
                            }
                        ),
                        contentDescription = "Rating $starRating",
                        tint = if (starRating <= rating) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CookedNoteHeader(
    recipe: Recipe,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
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
                .padding(16.dp)
                .size(40.dp)
                .background(
                    LightWhite65,
                    RoundedCornerShape(20.dp)
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
                .padding(16.dp)
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recipe.cuisine,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
