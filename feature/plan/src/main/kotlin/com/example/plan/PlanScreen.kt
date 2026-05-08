package com.example.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.design.theme.PalateColors
import com.example.domain.model.MealPlanItem
import com.example.domain.model.MealType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.design.R as DesignR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: PlanViewModel = hiltViewModel(),
    onNavigateToRecipe: (String, Boolean) -> Unit,
    onNavigateToMyRecipes: (String, String) -> Unit,
    onNavigateToSearch: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showSelectOptionDialog by remember { mutableStateOf<Pair<LocalDate, MealType>?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.plan_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            WeekSelector(
                currentWeekStart = uiState.currentWeekStart,
                onPreviousWeek = viewModel::previousWeek,
                onNextWeek = viewModel::nextWeek
            )
            Button(
                onClick = viewModel::assembleShoppingList,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(48.dp),
                enabled = !uiState.isShoppingListUpToDate && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PalateColors.GreenPrimary,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(com.example.design.R.drawable.shopping_cart),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.plan_assemble_list),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                val weekDays = (0..6).map { uiState.currentWeekStart.plusDays(it.toLong()) }
                items(weekDays) { date ->
                    DayPlanSection(
                        date = date,
                        meals = uiState.mealPlanItems.filter { it.date == date },
                        onSelectMeal = { mealType -> showSelectOptionDialog = date to mealType },
                        onDeleteMeal = { itemId -> showDeleteDialog = itemId },
                        onMealClick = { meal -> onNavigateToRecipe(meal.recipeId, meal.isUserRecipe) }
                    )
                }
            }
        }
    }

    // Dialogs
    showDeleteDialog?.let { itemId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.plan_delete_title)) },
            text = { Text(stringResource(R.string.plan_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeMeal(itemId)
                    showDeleteDialog = null
                }) {
                    Text(stringResource(R.string.plan_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(stringResource(R.string.plan_cancel))
                }
            }
        )
    }

    showSelectOptionDialog?.let { (date, mealType) ->
        AlertDialog(
            onDismissRequest = { showSelectOptionDialog = null },
            title = { Text(stringResource(R.string.plan_select_option_title)) },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.plan_option_my_recipes)) },
                        leadingContent = { Icon(Icons.Default.List, null) },
                        modifier = Modifier.clickable {
                            onNavigateToMyRecipes(date.toString(), mealType.name)
                            showSelectOptionDialog = null
                        }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.plan_option_search)) },
                        leadingContent = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.clickable {
                            onNavigateToSearch(date.toString(), mealType.name)
                            showSelectOptionDialog = null
                        }
                    )
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun WeekSelector(
    currentWeekStart: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    val weekEnd = currentWeekStart.plusDays(6)
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPreviousWeek, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.Gray)
        }
        Text(
            text = "${currentWeekStart.format(formatter)} - ${weekEnd.format(formatter)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        IconButton(onClick = onNextWeek, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun DayPlanSection(
    date: LocalDate,
    meals: List<MealPlanItem>,
    onSelectMeal: (MealType) -> Unit,
    onDeleteMeal: (String) -> Unit,
    onMealClick: (MealPlanItem) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dayNameRes = when (date.dayOfWeek) {
        java.time.DayOfWeek.MONDAY -> R.string.monday
        java.time.DayOfWeek.TUESDAY -> R.string.tuesday
        java.time.DayOfWeek.WEDNESDAY -> R.string.wednesday
        java.time.DayOfWeek.THURSDAY -> R.string.thursday
        java.time.DayOfWeek.FRIDAY -> R.string.friday
        java.time.DayOfWeek.SATURDAY -> R.string.saturday
        java.time.DayOfWeek.SUNDAY -> R.string.sunday
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(dayNameRes),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                date.format(formatter),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val standardMeals = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER)
            items(standardMeals) { type ->
                val meal = meals.find { it.mealType == type }
                MealSlot(
                    type = type,
                    meal = meal,
                    onSelect = { onSelectMeal(type) },
                    onDelete = { meal?.let { onDeleteMeal(it.id) } },
                    onMealClick = onMealClick
                )
            }
            
            val snacks = meals.filter { it.mealType == MealType.SNACK }
            items(snacks) { snack ->
                MealSlot(
                    type = MealType.SNACK,
                    meal = snack,
                    onSelect = {},
                    onDelete = { onDeleteMeal(snack.id) },
                    onMealClick = onMealClick
                )
            }
            
            item {
                AddSnackSlot(onAdd = { onSelectMeal(MealType.SNACK) })
            }
        }
    }
}

@Composable
fun MealSlot(
    type: MealType,
    meal: MealPlanItem?,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    onMealClick: (MealPlanItem) -> Unit
) {
    val typeRes = when (type) {
        MealType.BREAKFAST -> R.string.plan_breakfast
        MealType.LUNCH -> R.string.plan_lunch
        MealType.DINNER -> R.string.plan_dinner
        MealType.SNACK -> R.string.plan_snack
    }

    Column(
        modifier = Modifier.width(dimensionResource(R.dimen.plan_slot_width)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.plan_slot_height)),
            shape = RoundedCornerShape(dimensionResource(DesignR.dimen.recipe_card_corner_radius)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(
                width = dimensionResource(DesignR.dimen.recipe_card_border_width),
                color = MaterialTheme.colorScheme.outline
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { 
                        if (meal != null) onMealClick(meal) else onSelect()
                    }
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(typeRes),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (meal != null) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(DesignR.dimen.padding_small))
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(dimensionResource(DesignR.dimen.recipe_card_image_corner_radius)))
                    ) {
                        AsyncImage(
                            model = meal.recipeImageUrl,
                            contentDescription = meal.recipeName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(DesignR.drawable.ic_photo),
                            error = painterResource(DesignR.drawable.ic_photo)
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(dimensionResource(DesignR.dimen.padding_small))
                                .size(dimensionResource(DesignR.dimen.recipe_card_favorite_btn_size)),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.8f)
                        ) {
                            IconButton(onClick = onDelete) {
                                Icon(
                                    painter = painterResource(id = DesignR.drawable.ic_delete),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(dimensionResource(DesignR.dimen.recipe_card_favorite_icon_size))
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(DesignR.dimen.padding_small),
                                vertical = dimensionResource(DesignR.dimen.padding_extra_small)
                            )
                    ) {
                        Text(
                            text = meal.recipeName,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = dimensionResource(DesignR.dimen.text_size_recipe_name).value.sp
                            ),
                            lineHeight = dimensionResource(DesignR.dimen.nav_item_line_height).value.sp
                        )
                        Text(
                            text = meal.recipeCategory,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = dimensionResource(DesignR.dimen.text_size_small).value.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = dimensionResource(DesignR.dimen.recipe_card_border_width))
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(bottom = dimensionResource(DesignR.dimen.padding_medium))
                            .padding(horizontal = dimensionResource(DesignR.dimen.padding_medium))
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.plan_select),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddSnackSlot(onAdd: () -> Unit) {
    Card(
        modifier = Modifier
            .width(dimensionResource(R.dimen.plan_slot_width))
            .height(dimensionResource(R.dimen.plan_slot_height))
            .clickable(onClick = onAdd),
        shape = RoundedCornerShape(dimensionResource(R.dimen.plan_card_corner)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.plan_add_snack),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
