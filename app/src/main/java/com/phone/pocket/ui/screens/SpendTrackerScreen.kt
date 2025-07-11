package com.phone.pocket.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phone.pocket.data.Spend
import com.phone.pocket.data.CategorySummary
import com.phone.pocket.ui.components.SpendChart
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendTrackerScreen() {
    val context = LocalContext.current.applicationContext as Application
    val spendViewModel: SpendViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SpendViewModel(context) as T
        }
    })
    val spends by spendViewModel.spends.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showGraph by remember { mutableStateOf(false) }

    // Add CardViewModel to access cards
    val cardViewModel: CardViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CardViewModel(context) as T
        }
    })
    val cards by cardViewModel.cards.collectAsState()

    // Card spend breakdown, separated by credit and debit
    val creditCardBreakdown = spends.filter { it.paymentMode == "Card" && it.cardName != null && it.cardName!!.isNotBlank() && cards.any { c -> c.name == it.cardName && c.type.equals("Credit", ignoreCase = true) } }
        .groupBy { it.cardName ?: "Unknown" }
        .map { (card, spendList) ->
            card to spendList.sumOf { it.amount }
        }
        .sortedByDescending { it.second }
    val debitCardBreakdown = spends.filter { it.paymentMode == "Card" && it.cardName != null && it.cardName!!.isNotBlank() && cards.any { c -> c.name == it.cardName && c.type.equals("Debit", ignoreCase = true) } }
        .groupBy { it.cardName ?: "Unknown" }
        .map { (card, spendList) ->
            card to spendList.sumOf { it.amount }
        }
        .sortedByDescending { it.second }

    // Calculate category summaries for the chart
    val categorySummaries = remember(spends) {
        spends.groupBy { it.category }
            .map { (category, spendList) ->
                CategorySummary(
                    category = category,
                    total = spendList.sumOf { it.amount }
                )
            }
            .sortedByDescending { it.total }
    }

    // Group spends by category for expandable sections
    val spendsByCategory = spends.groupBy { it.category }
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            "Spend Tracker",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Spend")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Total Spends",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "₹${spends.sumOf { it.amount }}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            // Card Spends Section (move this block directly here)
            if (creditCardBreakdown.isNotEmpty() || debitCardBreakdown.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Card Spends", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            if (creditCardBreakdown.isNotEmpty()) {
                                Text("Credit Cards", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                                creditCardBreakdown.forEach { (card, total) ->
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF1976D2))
                                            Spacer(Modifier.width(8.dp))
                                            Text(card ?: "Unknown", style = MaterialTheme.typography.bodyMedium)
                                        }
                                        Text("₹$total", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            if (debitCardBreakdown.isNotEmpty()) {
                                Text("Debit Cards", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                                debitCardBreakdown.forEach { (card, total) ->
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF388E3C))
                                            Spacer(Modifier.width(8.dp))
                                            Text(card ?: "Unknown", style = MaterialTheme.typography.bodyMedium)
                                        }
                                        Text("₹$total", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Charts Section

            // Show the graph only if showGraph is true
            if (showGraph && spends.isNotEmpty()) {
                item {
                    SpendChart(
                        categorySummaries = categorySummaries,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Button to toggle graph visibility
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = { showGraph = !showGraph },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (showGraph) "Hide Spend Category Graph" else "Show Spend Category Graph")
                    }
                }
            }

            // Spends List Header
            item {
                Text(
                    text = "Recent Spends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Categorized spends with expandable sections
            spendsByCategory.forEach { (category, spendList) ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = categoryColor(category)
                        )
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedCategories[category] = !(expandedCategories[category] ?: false) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (expandedCategories[category] == true) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = if (expandedCategories[category] == true) "Collapse" else "Expand"
                                )
                            }
                            if (expandedCategories[category] == true) {
                                spendList.forEach { spend ->
                                    SpendCard(spend = spend)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddSpendDialog(
                onDismiss = { showAddDialog = false },
                onSpendAdded = { newSpend ->
                    spendViewModel.addSpend(newSpend)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun categoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food" -> Color(0xFFFFF3E0)
        "grocery" -> Color(0xFFE8F5E9)
        "entertainment" -> Color(0xFFE3F2FD)
        "transport" -> Color(0xFFFFEBEE)
        "shopping" -> Color(0xFFF3E5F5)
        "bills" -> Color(0xFFFFFDE7)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

@Composable
fun SpendCard(spend: Spend) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = spend.place,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(spend.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${spend.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                // Online/Offline Badge
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (spend.online) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = if (spend.online) "Online" else "Offline",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpendDialog(
    onDismiss: () -> Unit,
    onSpendAdded: (Spend) -> Unit
) {
    val context = LocalContext.current.applicationContext as Application
    val cardViewModel: CardViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CardViewModel(context) as T
        }
    })
    val cards by cardViewModel.cards.collectAsState()
    val cardNamesWithType = cards.map { it.name to it.type }

    var category by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Grocery", "Food", "Entertainment", "Transport", "Shopping", "Bills", "Other")

    var paymentMode by remember { mutableStateOf("") }
    var paymentDropdownExpanded by remember { mutableStateOf(false) }
    val paymentModes = listOf("Cash", "UPI", "Card")

    var selectedCard by remember { mutableStateOf("") }
    var cardDropdownExpanded by remember { mutableStateOf(false) }

    var amount by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var online by remember { mutableStateOf(false) }

    // Date input
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    var spendDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = spendDate)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { spendDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    "Add Spend",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = if (category == "Other") customCategory else category,
                        onValueChange = { if (category == "Other") customCategory = it },
                        label = { Text("Category") },
                        readOnly = category != "Other",
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                if (category == "Other") {
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Custom Category") }
                    )
                }
                // Payment Mode Dropdown
                ExposedDropdownMenuBox(
                    expanded = paymentDropdownExpanded,
                    onExpandedChange = { paymentDropdownExpanded = !paymentDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = paymentMode,
                        onValueChange = {},
                        label = { Text("Mode of Payment") },
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentDropdownExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = paymentDropdownExpanded,
                        onDismissRequest = { paymentDropdownExpanded = false }
                    ) {
                        paymentModes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    paymentMode = option
                                    paymentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                // Card Dropdown if payment mode is Card
                if (paymentMode == "Card") {
                    ExposedDropdownMenuBox(
                        expanded = cardDropdownExpanded,
                        onExpandedChange = { cardDropdownExpanded = !cardDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCard,
                            onValueChange = {},
                            label = { Text("Select Card") },
                            readOnly = true,
                            modifier = Modifier.menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = cardDropdownExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = cardDropdownExpanded,
                            onDismissRequest = { cardDropdownExpanded = false }
                        ) {
                            cardNamesWithType.forEach { (cardName, cardType) ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(cardName)
                                            Spacer(Modifier.width(8.dp))
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = if (cardType.equals("Credit", ignoreCase = true)) Color(0xFF1976D2) else Color(0xFF388E3C),
                                                tonalElevation = 2.dp
                                            ) {
                                                Text(
                                                    text = cardType,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedCard = cardName
                                        cardDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                // Place
                OutlinedTextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("Place") }
                )
                // Online/Offline
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = online, onCheckedChange = { online = it })
                    Text("Online Transaction")
                }
                // Date of Spend
                OutlinedTextField(
                    value = dateFormat.format(Date(spendDate)),
                    onValueChange = {},
                    label = { Text("Date of Spend") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.DateRange, contentDescription = "Pick Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalCategory = if (category == "Other") customCategory else category
                    if (finalCategory.isNotEmpty() && amount.isNotEmpty() && paymentMode.isNotEmpty()) {
                        val spend = Spend(
                            category = finalCategory,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            place = place,
                            online = online,
                            paymentMode = paymentMode,
                            cardName = if (paymentMode == "Card") selectedCard else null,
                            date = spendDate
                        )
                        onSpendAdded(spend)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 