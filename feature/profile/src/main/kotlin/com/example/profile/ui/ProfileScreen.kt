package com.example.profile.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.design.theme.PrimaryGreen
import com.example.profile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadAvatar(it, context.contentResolver)
        }
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = dimensionResource(com.example.design.R.dimen.padding_extra_large)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_extra_large)))

            // Header: Avatar and Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.profile_avatar_size))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.profile_avatar_radius)))
                        .background(color = MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    val avatarUrl = uiState.user?.avatarUrl
                    
                    Icon(
                        painter = painterResource(com.example.design.R.drawable.user),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.profile_avatar_icon_size)),
                        tint = if (uiState.isDarkMode) Color.LightGray else Color.Gray
                    )

                    if (!avatarUrl.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = avatarUrl),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = PrimaryGreen,
                            strokeWidth = 3.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(dimensionResource(com.example.design.R.dimen.padding_large)))

                Column {
                    Text(
                        text = uiState.user?.name ?: stringResource(R.string.default_user_name),
                        fontSize = dimensionResource(R.dimen.profile_user_name_text_size).value.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = uiState.user?.email ?: stringResource(R.string.default_user_email),
                        fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.height(dimensionResource(R.dimen.profile_button_height)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        ),
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(com.example.design.R.dimen.padding_large),
                            vertical = 0.dp
                        ),
                        shape = RoundedCornerShape(dimensionResource(com.example.design.R.dimen.radius_extra_large))
                    ) {
                        Icon(
                            painter = painterResource(com.example.design.R.drawable.upload),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(com.example.design.R.dimen.padding_medium)))
                        Text(
                            text = stringResource(R.string.upload_photo),
                            fontSize = dimensionResource(com.example.design.R.dimen.text_size_chip).value.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_extra_large)))

            // Statistics
            Text(
                text = stringResource(R.string.statistics_title),
                modifier = Modifier.fillMaxWidth(),
                fontSize = dimensionResource(R.dimen.profile_section_title_text_size).value.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.example.design.R.dimen.padding_medium))
            ) {
                StatCard(
                    value = uiState.cookedCount.toString(),
                    label = stringResource(R.string.stat_cooked),
                    color = PrimaryGreen,
                    valueColor = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = uiState.plannedCount.toString(),
                    label = stringResource(R.string.stat_planned),
                    color = MaterialTheme.colorScheme.primary,
                    valueColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = uiState.ownRecipesCount.toString(),
                    label = stringResource(R.string.stat_own_recipes),
                    color = if (uiState.isDarkMode) Color.White else MaterialTheme.colorScheme.outline,
                    valueColor = if (uiState.isDarkMode) Color.White else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_extra_large)))

            // Settings
            Text(
                text = stringResource(R.string.settings_title),
                modifier = Modifier.fillMaxWidth(),
                fontSize = dimensionResource(R.dimen.profile_section_title_text_size).value.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

            // Dark Theme Toggle
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.profile_settings_item_height)),
                shape = RoundedCornerShape(dimensionResource(com.example.design.R.dimen.radius_medium)),
                border = androidx.compose.foundation.BorderStroke(
                    dimensionResource(com.example.design.R.dimen.recipe_card_border_width),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(com.example.design.R.dimen.padding_large)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.dark_theme),
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                        fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.scale(0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_large)))

            // Language Selector
            var expanded by remember { mutableStateOf(false) }
            val languages = listOf(
                stringResource(R.string.language_russian),
                stringResource(R.string.language_english)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.profile_settings_item_height)),
                shape = RoundedCornerShape(dimensionResource(com.example.design.R.dimen.radius_medium)),
                border = androidx.compose.foundation.BorderStroke(
                    dimensionResource(com.example.design.R.dimen.recipe_card_border_width),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                color = MaterialTheme.colorScheme.surface,
                onClick = { expanded = true }
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(com.example.design.R.dimen.padding_large)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.language),
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Medium,
                            fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = uiState.language,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = lang,
                                        fontSize = dimensionResource(com.example.design.R.dimen.text_size_normal).value.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    viewModel.setLanguage(lang, context)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(com.example.design.R.dimen.padding_extra_large)))
        }

        // Logout Button
        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(com.example.design.R.dimen.padding_medium))
                .height(dimensionResource(R.dimen.profile_logout_button_height)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(dimensionResource(com.example.design.R.dimen.radius_extra_large))
        ) {
            Icon(
                painter = painterResource(com.example.design.R.drawable.move_item),
                contentDescription = null,
                modifier = Modifier.size(24.dp).rotate(180f)
            )
            Spacer(modifier = Modifier.width(dimensionResource(com.example.design.R.dimen.padding_medium)))
            Text(
                text = stringResource(R.string.logout_button),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    color: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(dimensionResource(R.dimen.profile_stat_card_height))
            .shadow(
                elevation = dimensionResource(R.dimen.profile_stat_card_elevation),
                shape = RoundedCornerShape(dimensionResource(R.dimen.profile_stat_card_radius))
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(dimensionResource(R.dimen.profile_stat_card_radius)),
        border = androidx.compose.foundation.BorderStroke(
            dimensionResource(com.example.design.R.dimen.recipe_card_border_width),
            color
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(com.example.design.R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = dimensionResource(R.dimen.profile_stat_value_text_size).value.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                fontSize = dimensionResource(com.example.design.R.dimen.text_size_chip).value.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = dimensionResource(R.dimen.profile_stat_label_line_height).value.sp
            )
        }
    }
}
