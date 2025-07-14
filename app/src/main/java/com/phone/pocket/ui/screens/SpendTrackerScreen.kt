// Enhanced SpendTrackerScreen.kt - Modern Material 3 UI
package com.phone.pocket.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phone.pocket.data.Spend
import com.phone.pocket.data.Card
import com.phone.pocket.ui.theme.*
import com.phone.pocket.ui.components.SpendChart
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendTrackerScreen(
    spends: List<Spend>,
    onAddSpend: (Spend) -> Unit,
    onDeleteSpend: (Spend) -> Unit,
    cards: List<Card> = emptyList(), // Add cards parameter
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val totalSpent = spends.sumOf { it.amount }
    val thisMonthSpends = spends.filter {
        val calendar = Calendar.getInstance()
        val spendDate = Calendar.getInstance().apply { timeInMillis = it.date }
        calendar.get(Calendar.MONTH) == spendDate.get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) == spendDate.get(Calendar.YEAR)
    }
    val thisMonthTotal = thisMonthSpends.sumOf { it.amount }
    // Compute category summaries for SpendChart
    val categorySummaries = spends.groupBy { it.category }.map { (category, spends) ->
        com.phone.pocket.data.CategorySummary(category, spends.sumOf { it.amount })
    }
    val listState = rememberLazyListState()
    val fabExtended = remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Spend Tracker",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Spend") },
                    text = { Text("Add Spend") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp),
                    expanded = fabExtended.value
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Summary Cards
            item {
                SpendSummarySection(
                    thisMonthTotal = thisMonthTotal
                )
            }

            // Chart Section
            if (spends.isNotEmpty()) {
                item {
                    SpendChartSection(categorySummaries = categorySummaries)
                }
            }

            // Recent Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Spend List
            items(spends.takeLast(10).reversed()) { spend ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    SpendItem(
                        spend = spend,
                        onEdit = { /* Handle edit */ },
                        onDelete = { onDeleteSpend(spend) }
                    )
                }
            }

            // Empty State
            if (spends.isEmpty()) {
                item {
                    EmptySpendState(
                        onAddSpend = { showAddDialog = true }
                    )
                }
            }
        }
    }

    // Add Spend Dialog
    if (showAddDialog) {
        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
            AddSpendDialog(
                onDismiss = { showAddDialog = false },
                onAddSpend = { spend ->
                    onAddSpend(spend)
                    showAddDialog = false
                },
                cards = cards // Pass cards to AddSpendDialog
            )
        }
    }
}

@Composable
fun SpendSummarySection(
    thisMonthTotal: Double
) {
    val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Text(
                "$month Spends",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                "₹${String.format("%.2f", thisMonthTotal)}",
                style = FinancialTypography.currencyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun SpendChartSection(categorySummaries: List<com.phone.pocket.data.CategorySummary>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Spending by Category",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Outlined.BarChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Component
            SpendChart(
                categorySummaries = categorySummaries,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

@Composable
fun SpendItem(
    spend: Spend,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon and Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(getCategoryColor(spend.category).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        getCategoryIcon(spend.category),
                        contentDescription = null,
                        tint = getCategoryColor(spend.category),
                        modifier = Modifier.size(24.dp)
                    )
                }
                // Spend Details
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        spend.place,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            spend.category,
                            color = getCategoryColor(spend.category)
                        )
                        Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(spend.date)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (spend.online) {
                            Icon(
                                Icons.Outlined.Language,
                                contentDescription = "Online",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            // Amount and Delete Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "-₹${String.format("%.2f", spend.amount)}",
                    style = FinancialTypography.currencySmall,
                    color = PocketColors.Expense,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete Spend", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun EmptySpendState(onAddSpend: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Outlined.Receipt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                "No expenses yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                "Start tracking your expenses by adding your first transaction",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            FilledTonalButton(
                onClick = onAddSpend,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add First Expense")
            }
        }
    }
}

// Helper function to get category icons
@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food" -> Icons.Outlined.Restaurant
        "transport" -> Icons.Outlined.DirectionsCar
        "shopping" -> Icons.Outlined.ShoppingBag
        "entertainment" -> Icons.Outlined.Movie
        "bills" -> Icons.Outlined.Receipt
        "health" -> Icons.Outlined.LocalHospital
        "education" -> Icons.Outlined.School
        "travel" -> Icons.Outlined.Flight
        else -> Icons.Outlined.Category
    }
}

// Add Spend Dialog (placeholder - would need full implementation)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpendDialog(
    onDismiss: () -> Unit,
    onAddSpend: (Spend) -> Unit,
    cards: List<Card> = emptyList() // Pass cards from CardsScreen
) {
    var category by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var online by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var paymentMode by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = remember { androidx.compose.material3.DatePickerState(initialSelectedDateMillis = date, locale = Locale.getDefault()) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var paymentModeExpanded by remember { mutableStateOf(false) }
    var cardDropdownExpanded by remember { mutableStateOf(false) }
    val categoryOptions = listOf(
        "Grocery", "Food", "Shopping", "Entertainment", "Travel", "Transport", "Utilities", "Bills", "Health", "Education", "Other"
    )
    val paymentModeOptions = listOf("Cash", "UPI", "Card", "Other")
    val cardOptions = cards.map { "${it.network} - ${it.number.takeLast(4)} (${it.type})" to it.name }
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    if (showDatePicker) {
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = it
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = if (category == "Other") customCategory else category,
                        onValueChange = {
                            if (category == "Other") customCategory = it else category = it
                        },
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor(),
                        isError = categoryError != null,
                        supportingText = { if (categoryError != null) Text(categoryError!!, color = MaterialTheme.colorScheme.error) }
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoryOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    category = it
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
                if (category == "Other") {
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Custom Category") },
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it.filter { c -> c.isDigit() || c == '.' }
                        amountError = if (amount.toDoubleOrNull() == null || (amount.toDoubleOrNull() ?: 0.0) <= 0.0) "Enter a valid amount" else null
                    },
                    label = { Text("Amount") },
                    singleLine = true,
                    isError = amountError != null,
                    supportingText = { if (amountError != null) Text(amountError!!, color = MaterialTheme.colorScheme.error) }
                )
                OutlinedTextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("Place") },
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = online, onCheckedChange = { online = it })
                    Text("Online Transaction")
                }
                ExposedDropdownMenuBox(
                    expanded = paymentModeExpanded,
                    onExpandedChange = { paymentModeExpanded = !paymentModeExpanded }
                ) {
                    OutlinedTextField(
                        value = paymentMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Mode") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentModeExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = paymentModeExpanded,
                        onDismissRequest = { paymentModeExpanded = false }
                    ) {
                        paymentModeOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    paymentMode = it
                                    paymentModeExpanded = false
                                }
                            )
                        }
                    }
                }
                if (paymentMode == "Card" && cardOptions.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = cardDropdownExpanded,
                        onExpandedChange = { cardDropdownExpanded = !cardDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = cardOptions.find { it.second == cardName }?.first ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Card") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cardDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = cardDropdownExpanded,
                            onDismissRequest = { cardDropdownExpanded = false }
                        ) {
                            cardOptions.forEach { (display, name) ->
                                DropdownMenuItem(
                                    text = { Text(display) },
                                    onClick = {
                                        cardName = name
                                        cardDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    singleLine = false
                )
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Pick Date: " + java.text.SimpleDateFormat("MMM dd, yyyy").format(java.util.Date(date)))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val spend = Spend(
                        category = if (category == "Other") customCategory else category,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        place = place,
                        online = online,
                        date = date,
                        paymentMode = paymentMode,
                        cardName = if (cardName.isBlank()) null else cardName,
                        notes = if (notes.isBlank()) null else notes
                    )
                    onAddSpend(spend)
                },
                enabled = (if (category == "Other") customCategory.isNotBlank() else category.isNotBlank()) &&
                    amount.toDoubleOrNull() != null && (amount.toDoubleOrNull() ?: 0.0) > 0.0 &&
                    place.isNotBlank() && paymentMode.isNotBlank() && (paymentMode != "Card" || cardName.isNotBlank())
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}