package com.example.recipe_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
            NoteSectionTitle(stringResource(R.string.recipe_detail_ingredients))
        }

        itemsIndexed(recipe.ingredients) { _, ingredient ->
            NoteIngredientItem(ingredient.name, ingredient.amount)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            NoteSectionTitle(stringResource(R.string.recipe_detail_preparation))
        }

        itemsIndexed(recipe.instructions) { _, step ->
            NotePreparationStep(step)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            NoteSectionTitle(stringResource(R.string.my_notes_section))
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
    val ratingLabel = when (rating) {
        1 -> stringResource(R.string.rating_bad)
        2 -> stringResource(R.string.rating_poor)
        3 -> stringResource(R.string.rating_average)
        4 -> stringResource(R.string.rating_good)
        5 -> stringResource(R.string.rating_excellent)
        else -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.your_rating),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val starRating = index + 1
                        val isSelected = starRating <= rating
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Rating $starRating",
                            tint = if (isSelected) PalateColors.GreenPrimary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onRatingChange(starRating) }
                        )
                    }
                }

                if (ratingLabel.isNotEmpty()) {
                    Text(
                        text = ratingLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PalateColors.GreenPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteSectionTitle(title: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun NoteIngredientItem(name: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            modifier = Modifier.width(16.dp),
            fontSize = 16.sp
        )
        Text(
            text = "$name — $amount",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NotePreparationStep(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
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
