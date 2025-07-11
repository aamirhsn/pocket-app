package com.phone.pocket.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import com.phone.pocket.data.Card
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalClipboardManager
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen() {
    val context = LocalContext.current.applicationContext as Application
    val cardViewModel: CardViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CardViewModel(context) as T
        }
    })
    val cards by cardViewModel.cards.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val creditCards = cards.filter { it.type.equals("Credit", ignoreCase = true) }
    val debitCards = cards.filter { it.type.equals("Debit", ignoreCase = true) }

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
                            "Cards",
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
                Icon(Icons.Filled.Add, contentDescription = "Add Card")
            }
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
        ) {
            // Card count at the top
            val totalCards = creditCards.size + debitCards.size
            if (totalCards > 0) {
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
                            text = "Total Cards",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "$totalCards",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            // Credit Cards
            if (creditCards.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD) // Subtle blue for credit
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Credit Cards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            creditCards.forEach { card -> CardItem(card = card) }
                        }
                    }
                }
            }
            // Debit Cards
            if (debitCards.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9) // Subtle green for debit
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Debit Cards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            debitCards.forEach { card -> CardItem(card = card) }
                        }
                    }
                }
            }
            if (creditCards.isEmpty() && debitCards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No cards yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap + to add your first card",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        if (showAddDialog) {
            AddCardDialog(
                onDismiss = { showAddDialog = false },
                onCardAdded = { newCard ->
                    cardViewModel.addCard(newCard)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun CardItem(card: Card, modifier: Modifier = Modifier) {
    var showSensitive by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    fun formatCardNumber(number: String, masked: Boolean): String {
        val digits = number.filter { it.isDigit() }
        return if (masked && digits.length == 16) {
            "${digits.substring(0, 4)} •••••••• ${digits.substring(12, 16)}"
        } else {
            digits.chunked(4).joinToString(" ")
        }
    }

    // Choose badge color by network
    val networkColors = mapOf(
        "Visa" to Color(0xFF1A1F71),
        "Mastercard" to Color(0xFFFF5F00),
        "Rupay" to Color(0xFF0054A6),
        "Amex" to Color(0xFF2E77BB),
        "Discover" to Color(0xFFFF6000),
        "Diners" to Color(0xFF006272),
        "JCB" to Color(0xFF007B43)
    )
    val badgeColor = networkColors[card.network] ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                // Network badge
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = badgeColor,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = card.network,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Card Number (masked or shown) - big and bold
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatCardNumber(card.number, masked = !showSensitive),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(card.number))
                    Toast.makeText(context, "Card number copied", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy card number")
                }
                IconButton(onClick = { showSensitive = !showSensitive }) {
                    Icon(
                        imageVector = if (showSensitive) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showSensitive) "Hide" else "Show"
                    )
                }
            }
            // Expiry and CVV
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (showSensitive) "Expiry: ${card.expiry}" else "Expiry: ••/••",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CVV: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (showSensitive) card.cvv else "•••",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onCardAdded: (Card) -> Unit
) {
    var cardName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf("Credit") }
    var cardNetwork by remember { mutableStateOf("Visa") }
    var showCardNumber by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val cardTypes = listOf("Credit", "Debit")
    val cardNetworks = listOf("Visa", "Mastercard", "Rupay", "Amex", "Discover", "Diners", "JCB")
    var networkDropdownExpanded by remember { mutableStateOf(false) }

    // Visual transformation for card number (groups of 4)
    val cardNumberTransformation = VisualTransformation { text ->
        val trimmed = text.text.take(16)
        val sb = StringBuilder()
        var originalToTransformed = IntArray(trimmed.length + 1)
        var transformedToOriginal = IntArray(trimmed.length + 4) // max 3 spaces
        var tIndex = 0
        for (i in trimmed.indices) {
            if (i > 0 && i % 4 == 0) {
                sb.append(' ')
                tIndex++
            }
            sb.append(trimmed[i])
            originalToTransformed[i] = tIndex
            transformedToOriginal[tIndex] = i
            tIndex++
        }
        originalToTransformed[trimmed.length] = tIndex
        transformedToOriginal[tIndex] = trimmed.length
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset <= trimmed.length) originalToTransformed[offset] else sb.length
            }
            override fun transformedToOriginal(offset: Int): Int {
                return if (offset <= sb.length) transformedToOriginal[offset] else trimmed.length
            }
        }
        TransformedText(AnnotatedString(sb.toString()), offsetMapping)
    }
    // Visual transformation for expiry (MM/YY)
    val expiryTransformation = VisualTransformation { text ->
        val digits = text.text.filter { it.isDigit() }
        val formatted = when {
            digits.length == 0 -> ""
            digits.length == 1 -> digits
            digits.length == 2 -> digits
            digits.length == 3 -> digits.substring(0, 2.coerceAtMost(digits.length)) + "/" + digits.substring(2, 3.coerceAtMost(digits.length))
            digits.length >= 4 -> digits.substring(0, 2.coerceAtMost(digits.length)) + "/" + digits.substring(2, 4.coerceAtMost(digits.length))
            else -> digits
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset in 3..4 -> offset + 1 // account for slash
                    else -> formatted.length
                }
            }
            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset in 3..5 -> offset - 1 // account for slash
                    else -> digits.length
                }
            }
        }
        TransformedText(AnnotatedString(formatted), offsetMapping)
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
                    "Add New Card",
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
                // Card Name
                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text("Card Name") }
                )
                // Card Number
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = {
                        val digits = it.filter { c -> c.isDigit() }
                        if (digits.length <= 16) cardNumber = digits
                    },
                    label = { Text("Card Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = cardNumberTransformation
                )
                // Expiry Date with MM/YY masking, no digit swap
                OutlinedTextField(
                    value = expiry,
                    onValueChange = {
                        val digits = it.filter { c -> c.isDigit() }
                        if (digits.length <= 4) expiry = digits
                    },
                    label = { Text("Expiry (MM/YY)") },
                    placeholder = { Text("12/25") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = expiryTransformation
                )
                // CVV (3 digits max)
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3) cvv = it.filter { c -> c.isDigit() } },
                    label = { Text("CVV") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation()
                )
                // Card Type
                ExposedDropdownMenuBox(
                    expanded = networkDropdownExpanded,
                    onExpandedChange = { networkDropdownExpanded = !networkDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = cardNetwork,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Card Network") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = networkDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = networkDropdownExpanded,
                        onDismissRequest = { networkDropdownExpanded = false }
                    ) {
                        cardNetworks.forEach { network ->
                            DropdownMenuItem(
                                text = { Text(network) },
                                onClick = {
                                    cardNetwork = network
                                    networkDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                TextButton(
                    onClick = {
                        errorMessage = ""
                        when {
                            cardName.isEmpty() -> errorMessage = "Card name is required."
                            cardNumber.length != 16 -> errorMessage = "Card number must be 16 digits."
                            expiry.length != 4 -> errorMessage = "Expiry must be 4 digits (MMYY)."
                            cvv.length != 3 -> errorMessage = "CVV must be 3 digits."
                            cardType != "Credit" && cardType != "Debit" -> errorMessage = "Card type must be Credit or Debit."
                            else -> {
                                val formattedExpiry = expiry.substring(0, 2) + "/" + expiry.substring(2, 4)
                                val card = Card(
                                    name = cardName,
                                    number = cardNumber,
                                    expiry = formattedExpiry,
                                    cvv = cvv,
                                    type = cardType,
                                    network = cardNetwork
                                )
                                onCardAdded(card)
                            }
                        }
                    }
                ) {
                    Text("Add")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 