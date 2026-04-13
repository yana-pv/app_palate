package com.example.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.design.R
import com.example.theme.GrayText
import com.example.theme.SecondaryPurple
import com.example.theme.SurfaceWhite

sealed class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val icon: @Composable () -> ImageVector
) {
    object Plan : BottomNavItem("plan", R.string.nav_plan, { ImageVector.vectorResource(R.drawable.calendar) })
    object MyRecipes : BottomNavItem("my_recipes", R.string.nav_my_recipes, { ImageVector.vectorResource(R.drawable.book_open) })
    object Home : BottomNavItem("home", R.string.nav_home, { Icons.Default.Search })
    object ShoppingList : BottomNavItem("shopping_list", R.string.nav_shopping_list, { ImageVector.vectorResource(R.drawable.shopping_cart) })
    object Profile : BottomNavItem("profile", R.string.nav_profile, { ImageVector.vectorResource(R.drawable.user) })
}

@Composable
fun PalateBottomNav(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Plan,
        BottomNavItem.MyRecipes,
        BottomNavItem.Home,
        BottomNavItem.ShoppingList,
        BottomNavItem.Profile
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.nav_bar_height)),
        color = SurfaceWhite,
        shadowElevation = dimensionResource(R.dimen.nav_bar_elevation)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                NavItem(
                    modifier = Modifier.weight(1f),
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    modifier: Modifier = Modifier,
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val title = stringResource(item.titleRes)
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = dimensionResource(R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = dimensionResource(R.dimen.nav_item_indicator_width),
                    height = dimensionResource(R.dimen.nav_item_indicator_height)
                )
                .clip(RoundedCornerShape(dimensionResource(R.dimen.nav_item_indicator_corner_radius)))
                .background(if (isSelected) SecondaryPurple else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon(),
                contentDescription = title,
                tint = if (isSelected) Color.White else GrayText,
                modifier = Modifier.size(dimensionResource(R.dimen.nav_item_icon_size))
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
        Text(
            text = title,
            fontSize = 10.sp,
            color = if (isSelected) SecondaryPurple else GrayText,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}