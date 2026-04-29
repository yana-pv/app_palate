package com.example.shopping_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.design.theme.*
import com.example.domain.model.ShoppingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var nameText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var unitText by remember { mutableStateOf("") }

    val borderColor = LightGrayBorder

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.shopping_list_title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = { viewModel.showDeleteConfirmation(true) }) {
                        Icon(
                            painter = painterResource(com.example.design.R.drawable.delete),
                            contentDescription = stringResource(R.string.delete_selected),
                            tint = Color.Red,
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size))
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = dimensionResource(R.dimen.padding_large))
        ) {
            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_extra_small), bottom = dimensionResource(R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                CustomInputField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    placeholder = stringResource(R.string.item_name_hint),
                    modifier = Modifier.weight(2f),
                    borderColor = borderColor
                )

                CustomInputField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    placeholder = stringResource(R.string.item_amount_hint),
                    modifier = Modifier.weight(1.2f),
                    borderColor = borderColor,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                CustomInputField(
                    value = unitText,
                    onValueChange = { unitText = it },
                    placeholder = stringResource(R.string.item_unit_hint),
                    modifier = Modifier.weight(1f),
                    borderColor = borderColor
                )

                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.input_row_height))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.input_corner_radius)))
                        .background(PrimaryGreen)
                        .clickable {
                            if (nameText.isNotBlank()) {
                                viewModel.addItem(nameText, amountText, unitText)
                                nameText = ""
                                amountText = ""
                                unitText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_item_desc),
                        tint = Color.White,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size))
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                    contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.shopping_list_bottom_padding))
                ) {
                    items(uiState.items) { item ->
                        ShoppingItemRow(
                            item = item,
                            onCheckedChange = { viewModel.toggleItemChecked(item) },
                            onEditClick = { viewModel.startEditing(item) }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteConfirmation(false) },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.deleteCheckedItems()
                    viewModel.showDeleteConfirmation(false)
                }) {
                    Text(stringResource(R.string.yes), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteConfirmation(false) }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Edit Dialog
    uiState.editingItem?.let { item ->
        EditItemDialog(
            item = item,
            onDismiss = { viewModel.startEditing(null) },
            onConfirm = { name, amount, unit ->
                viewModel.updateItem(item.id, name, amount, unit)
                viewModel.startEditing(null)
            }
        )
    }
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    borderColor: Color,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(dimensionResource(R.dimen.input_row_height))
            .border(dimensionResource(R.dimen.border_width), borderColor, RoundedCornerShape(dimensionResource(R.dimen.input_corner_radius))),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = TextStyle(fontSize = dimensionResource(R.dimen.input_text_size).value.sp, color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(PrimaryGreen),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontSize = dimensionResource(R.dimen.placeholder_text_size).value.sp, color = borderColor)
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit
) {
    val backgroundColor = if (item.isChecked) LightCheckedItemBg else Color.Transparent
    val shape = RoundedCornerShape(dimensionResource(R.dimen.input_corner_radius))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(dimensionResource(R.dimen.border_width), Color.LightGray, shape)
            .padding(vertical = dimensionResource(R.dimen.padding_extra_small) + 2.dp, horizontal = dimensionResource(R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.icon_size))
                .clickable { onCheckedChange(!item.isChecked) },
            contentAlignment = Alignment.Center
        ) {
            if (item.isChecked) {
                Icon(
                    painter = painterResource(com.example.design.R.drawable.selected),
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.icon_size_small))
                        .border(dimensionResource(R.dimen.border_width_thin), Color.LightGray, androidx.compose.foundation.shape.CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_medium)))
        
        val displayText = if (item.amount.isEmpty() && item.unit.isEmpty()) {
            item.name
        } else {
            "${item.name} - ${item.amount} ${item.unit}"
        }

        Text(
            text = displayText,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                color = if (item.isChecked) Color.Gray else MaterialTheme.colorScheme.onSurface
            )
        )
        
        IconButton(
            onClick = onEditClick,
            modifier = Modifier.size(dimensionResource(R.dimen.button_size_small))
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_title),
                tint = Color.Gray,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small))
            )
        }
    }
}

@Composable
fun EditItemDialog(
    item: ShoppingItem,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var amount by remember { mutableStateOf(item.amount) }
    var unit by remember { mutableStateOf(item.unit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text(stringResource(R.string.item_name_hint)) },
                    shape = RoundedCornerShape(dimensionResource(R.dimen.input_corner_radius))
                )
                OutlinedTextField(
                    value = amount, 
                    onValueChange = { amount = it }, 
                    label = { Text(stringResource(R.string.item_amount_hint)) },
                    shape = RoundedCornerShape(dimensionResource(R.dimen.input_corner_radius))
                )
                OutlinedTextField(
                    value = unit, 
                    onValueChange = { unit = it }, 
                    label = { Text(stringResource(R.string.item_unit_hint)) },
                    shape = RoundedCornerShape(dimensionResource(R.dimen.input_corner_radius))
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, amount, unit) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(dimensionResource(R.dimen.input_corner_radius))
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
