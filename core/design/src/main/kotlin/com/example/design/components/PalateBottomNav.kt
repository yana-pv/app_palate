package com.example.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.design.R

data class BottomNavItemData(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector
)

@Composable
fun PalateBottomNav(
    items: List<BottomNavItemData>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = dimensionResource(R.dimen.nav_bar_elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(dimensionResource(R.dimen.nav_bar_height_small)),
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
fun NavItem(
    modifier: Modifier = Modifier,
    item: BottomNavItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val title = stringResource(item.titleRes)
    val contentColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = dimensionResource(R.dimen.padding_small)),
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
                .background(if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(dimensionResource(R.dimen.nav_item_icon_size))
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
        Text(
            text = title,
            fontSize = 10.sp,
            color = contentColor,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}