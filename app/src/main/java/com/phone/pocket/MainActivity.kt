package com.phone.pocket

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.phone.pocket.ui.theme.PocketTheme
import com.phone.pocket.ui.screens.SpendTrackerScreen
import com.phone.pocket.ui.screens.CardsScreen
import com.phone.pocket.ui.screens.SplashScreen
import com.phone.pocket.auth.BiometricHelper
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import android.os.Build
import android.graphics.RenderEffect as FrameworkRenderEffect
import android.graphics.Shader as FrameworkShader

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val biometricHelper = BiometricHelper(this)

        setContent {
            PocketTheme {
                // Compose state for authentication
                var isAuthenticated by remember { mutableStateOf(false) }
                var showError by remember { mutableStateOf<String?>(null) }
                var showSplash by remember { mutableStateOf(true) }

                // Launch biometric prompt ONCE when splash is shown
                LaunchedEffect(showSplash) {
                    if (showSplash) {
                        if (biometricHelper.isBiometricAvailable()) {
                            // Must run on UI thread
                            lifecycleScope.launch {
                                biometricHelper.showBiometricPrompt(
                                    activity = this@MainActivity,
                                    onSuccess = {
                                        isAuthenticated = true
                                        showSplash = false
                                    },
                                    onError = { error ->
                                        showError = error
                                        showSplash = false
                                    },
                                    onFailed = {
                                        showError = "Authentication failed. Try again."
                                        showSplash = false
                                    }
                                )
                            }
                        } else {
                            // No biometric, proceed
                            isAuthenticated = true
                            showSplash = false
                        }
                    }
                }

                when {
                    showSplash -> SplashScreen()
                    isAuthenticated -> MainAppContent()
                    else -> {
                        // Show error dialog if authentication fails
                        showError?.let { error ->
                            androidx.compose.material3.AlertDialog(
                                onDismissRequest = { showError = null },
                                title = { Text("Authentication Error") },
                                text = { Text(error) },
                                confirmButton = {
                                    androidx.compose.material3.TextButton(
                                        onClick = { showError = null }
                                    ) {
                                        Text("OK")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem(
            name = "Spend Tracker",
            route = "spend_tracker",
            icon = Icons.Filled.AccountBalanceWallet
        ),
        BottomNavItem(
            name = "Cards",
            route = "cards",
            icon = Icons.Filled.CreditCard
        )
    )
    Scaffold(
        bottomBar = {
            FrostedNavBar {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.name) },
                        label = { Text(item.name) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "spend_tracker",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("spend_tracker") { SpendTrackerScreen() }
            composable("cards") { CardsScreen() }
        }
    }
}

@Composable
fun FrostedNavBar(content: @Composable RowScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White.copy(alpha = 0.6f)) // semi-transparent white for frosted look
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            content = content
        )
    }
}

// Bottom navigation item data class
data class BottomNavItem(val name: String, val route: String, val icon: ImageVector)