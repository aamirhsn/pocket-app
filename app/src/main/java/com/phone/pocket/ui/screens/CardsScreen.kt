// Enhanced CardsScreen.kt - Modern Material 3 UI
package com.phone.pocket.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phone.pocket.data.Card
import com.phone.pocket.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import com.phone.pocket.data.Spend
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    cards: List<Card>,
    spends: List<Spend>,
    onAddCard: (Card) -> Unit,
    onEditCard: (Card) -> Unit,
    onDeleteCard: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var cardToEdit by remember { mutableStateOf<Card?>(null) }
    var cardToDelete by remember { mutableStateOf<Card?>(null) }
    val listState = rememberLazyListState()
    val fabExtended = remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Cards",
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
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Card") },
                    text = { Text("Add Card") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp),
                    expanded = fabExtended.value
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CardSpendsSummary(cards = cards, spends = spends)
            Spacer(Modifier.height(16.dp))
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Cards List Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Your Cards",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "${cards.size} cards",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Cards List
                items(cards) { card ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ModernCardItem(
                            card = card,
                            onEdit = { cardToEdit = card },
                            onDelete = { cardToDelete = card }
                        )
                    }
                }
                // Empty State
                if (cards.isEmpty()) {
                    item {
                        EmptyCardsState(
                            onAddCard = { showAddDialog = true }
                        )
                    }
                }
            }
        }
    }
    // Add/Edit Card Dialog
    if (showAddDialog || cardToEdit != null) {
        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
            AddCardDialog(
                onDismiss = {
                    showAddDialog = false
                    cardToEdit = null
                },
                onAddCard = {
                    onAddCard(it)
                    showAddDialog = false
                    cardToEdit = null
                },
                cardToEdit = cardToEdit
            )
        }
    }
    // Delete Card Dialog
    if (cardToDelete != null) {
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("Delete Card") },
            text = { Text("Are you sure you want to delete this card?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteCard(cardToDelete!!)
                    cardToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CardSpendsSummary(cards: List<Card>, spends: List<Spend>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Spends by Card", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            for (card in cards) {
                val total = spends.filter { it.cardName == card.name }.sumOf { it.amount }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(card.name, style = MaterialTheme.typography.bodyMedium)
                    Text("₹${String.format("%.2f", total)}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun ModernCardItem(
    card: Card,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }
    val cardTypeColor = getCardTypeColor(card.type)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { showDetails = !showDetails },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            cardTypeColor,
                            cardTypeColor.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = card.number.chunked(4).joinToString(" "),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Expiry",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = card.expiry,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Column {
                        Text(
                            text = "CVV",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = card.cvv,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCardsState(onAddCard: () -> Unit) {
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
                Icons.Outlined.CreditCard,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "No cards yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Start by adding your first card.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            FilledTonalButton(
                onClick = onAddCard,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Card")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onAddCard: (Card) -> Unit,
    cardToEdit: Card? = null
) {
    var name by remember { mutableStateOf(cardToEdit?.name ?: "") }
    var number by remember { mutableStateOf(cardToEdit?.number ?: "") }
    var expiry by remember { mutableStateOf(cardToEdit?.expiry ?: "") }
    var cvv by remember { mutableStateOf(cardToEdit?.cvv ?: "") }
    var type by remember { mutableStateOf(cardToEdit?.type ?: "") }
    var network by remember { mutableStateOf(cardToEdit?.network ?: "") }
    var typeExpanded by remember { mutableStateOf(false) }
    var networkExpanded by remember { mutableStateOf(false) }
    val typeOptions = listOf("Credit", "Debit")
    val networkOptions = listOf("Visa", "Mastercard", "RuPay", "Amex", "Discover", "Other")
    var expiryError by remember { mutableStateOf<String?>(null) }
    var numberError by remember { mutableStateOf<String?>(null) }
    var cvvError by remember { mutableStateOf<String?>(null) }

    fun validateExpiry(input: String): Boolean {
        return Regex("^(0[1-9]|1[0-2])/\\d{2}").matches(input)
    }
    fun validateCardNumber(input: String): Boolean {
        return input.filter { it.isDigit() }.length == 16
    }
    fun validateCVV(input: String): Boolean {
        return input.length == 3 && input.all { it.isDigit() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (cardToEdit == null) "Add Card" else "Edit Card") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = {
                        val digits = it.filter { c -> c.isDigit() }.take(16)
                        number = digits
                        numberError = if (digits.length == 16) null else "Card number must be 16 digits"
                    },
                    label = { Text("Number") },
                    singleLine = true,
                    isError = numberError != null,
                    supportingText = { if (numberError != null) Text(numberError!!, color = MaterialTheme.colorScheme.error) },
                    visualTransformation = { text ->
                        val trimmed = text.text.take(16)
                        val formatted = trimmed.chunked(4).joinToString(" ")
                        val offsetMapping = object : OffsetMapping {
                            override fun originalToTransformed(offset: Int): Int {
                                var spaces = 0
                                for (i in 1..(offset / 4)) {
                                    if (i * 4 < trimmed.length + 1) spaces++
                                }
                                return offset + spaces
                            }
                            override fun transformedToOriginal(offset: Int): Int {
                                return offset - (0 until offset).count { (it + 1) % 5 == 0 }
                            }
                        }
                        TransformedText(AnnotatedString(formatted), offsetMapping)
                    },
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                OutlinedTextField(
                    value = expiry,
                    onValueChange = {
                        var value = it.filter { c -> c.isDigit() }
                        if (value.length > 4) value = value.take(4)
                        expiry = if (value.length > 2) value.substring(0,2) + "/" + value.substring(2) else value
                        expiryError = if (validateExpiry(expiry)) null else "MM/YY format required"
                    },
                    label = { Text("Expiry (MM/YY)") },
                    singleLine = true,
                    isError = expiryError != null,
                    supportingText = { if (expiryError != null) Text(expiryError!!, color = MaterialTheme.colorScheme.error) },
                    visualTransformation = { text ->
                        val trimmed = text.text.filter { it.isDigit() }.take(4)
                        val formatted = if (trimmed.length > 2) trimmed.substring(0,2) + "/" + trimmed.substring(2) else trimmed
                        val offsetMapping = object : OffsetMapping {
                            override fun originalToTransformed(offset: Int): Int {
                                return if (offset <= 2) offset else offset + 1
                            }
                            override fun transformedToOriginal(offset: Int): Int {
                                return if (offset <= 2) offset else offset - 1
                            }
                        }
                        TransformedText(AnnotatedString(formatted), offsetMapping)
                    },
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                OutlinedTextField(
                    value = cvv,
                    onValueChange = {
                        val digits = it.filter { c -> c.isDigit() }.take(3)
                        cvv = digits
                        cvvError = if (digits.length == 3) null else "CVV must be 3 digits"
                    },
                    label = { Text("CVV") },
                    singleLine = true,
                    isError = cvvError != null,
                    supportingText = { if (cvvError != null) Text(cvvError!!, color = MaterialTheme.colorScheme.error) }
                )
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        typeOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    type = it
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = networkExpanded,
                    onExpandedChange = { networkExpanded = !networkExpanded }
                ) {
                    OutlinedTextField(
                        value = network,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Network") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = networkExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = networkExpanded,
                        onDismissRequest = { networkExpanded = false }
                    ) {
                        networkOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    network = it
                                    networkExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val card = Card(
                        name = name,
                        number = number,
                        expiry = expiry,
                        cvv = cvv,
                        type = type,
                        network = network
                    )
                    onAddCard(card)
                },
                enabled = name.isNotBlank() &&
                    validateCardNumber(number) &&
                    validateExpiry(expiry) &&
                    validateCVV(cvv) &&
                    type.isNotBlank() &&
                    network.isNotBlank()
            ) {
                Text(if (cardToEdit == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun getCardTypeColor(cardType: String): Color {
    return when (cardType.lowercase()) {
        "credit" -> PocketColors.CreditCard
        "debit" -> PocketColors.DebitCard
        "prepaid" -> PocketColors.PrepaidCard
        else -> PocketColors.CreditCard
    }
}