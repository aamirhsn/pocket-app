// Enhanced Theme.kt - Material 3 Expressive Theme with Dynamic Colors
package com.phone.pocket.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.CubicBezierEasing

// Theme Configuration
object PocketThemeConfig {
    val isDynamicColorEnabled = MutableStateFlow(true)
    val isAmoledMode = MutableStateFlow(false)
    val accentColor = MutableStateFlow(AccentColor.Purple)

    enum class AccentColor(val lightColor: Color, val darkColor: Color) {
        Purple(PocketColors.Primary40, PocketColors.Primary80),
        Blue(Color(0xFF1976D2), Color(0xFF90CAF9)),
        Green(Color(0xFF388E3C), Color(0xFFA5D6A7)),
        Pink(Color(0xFFE91E63), Color(0xFFF48FB1)),
        Orange(Color(0xFFFF9800), Color(0xFFFFCC02)),
        Teal(Color(0xFF00BCD4), Color(0xFF4DD0E1))
    }
}

@Composable
fun PocketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isDynamicColorEnabled by PocketThemeConfig.isDynamicColorEnabled.collectAsStateWithLifecycle()
    val isAmoledMode by PocketThemeConfig.isAmoledMode.collectAsStateWithLifecycle()
    val accentColor by PocketThemeConfig.accentColor.collectAsStateWithLifecycle()

    val colorScheme = when {
        // Dynamic color is available on Android 12+
        dynamicColor && isDynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                val scheme = dynamicDarkColorScheme(context)
                if (isAmoledMode) scheme.copy(
                    surface = Color.Black,
                    background = Color.Black,
                    surfaceContainerLowest = Color.Black,
                    surfaceContainerLow = Color(0xFF0A0A0A),
                    surfaceContainer = Color(0xFF121212),
                    surfaceContainerHigh = Color(0xFF1A1A1A),
                    surfaceContainerHighest = Color(0xFF222222)
                ) else scheme
            } else {
                dynamicLightColorScheme(context)
            }
        }
        darkTheme -> {
            val scheme = DarkColorScheme.copy(
                primary = accentColor.darkColor,
                primaryContainer = accentColor.darkColor.copy(alpha = 0.3f)
            )
            if (isAmoledMode) scheme.copy(
                surface = Color.Black,
                background = Color.Black,
                surfaceContainerLowest = Color.Black,
                surfaceContainerLow = Color(0xFF0A0A0A),
                surfaceContainer = Color(0xFF121212),
                surfaceContainerHigh = Color(0xFF1A1A1A),
                surfaceContainerHighest = Color(0xFF222222)
            ) else scheme
        }
        else -> LightColorScheme.copy(
            primary = accentColor.lightColor,
            primaryContainer = accentColor.lightColor.copy(alpha = 0.1f)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PocketTypography,
        shapes = PocketShapes,
        content = content
    )
}

// Enhanced Shapes for Material 3 Expressive
val PocketShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Custom shapes for specific components
object CustomShapes {
    val cardShape = RoundedCornerShape(20.dp)
    val buttonShape = RoundedCornerShape(24.dp)
    val chipShape = RoundedCornerShape(16.dp)
    val dialogShape = RoundedCornerShape(24.dp)
    val bottomSheetShape = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    val fabShape = RoundedCornerShape(16.dp)
    val textFieldShape = RoundedCornerShape(12.dp)
}

// Material 3 Expressive Motion
object PocketMotion {
    const val FAST_DURATION = 150
    const val MEDIUM_DURATION = 300
    const val SLOW_DURATION = 500
    const val EXTRA_SLOW_DURATION = 1000

    val FastEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    val MediumEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val SlowEasing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
}

// Theme utilities and extensions
@Composable
fun isPocketThemeLight(): Boolean = !isSystemInDarkTheme()

@Composable
fun isPocketThemeDark(): Boolean = isSystemInDarkTheme()

@Composable
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food" -> PocketColors.FoodColor
        "transport" -> PocketColors.TransportColor
        "shopping" -> PocketColors.ShoppingColor
        "entertainment" -> PocketColors.EntertainmentColor
        "bills" -> PocketColors.BillsColor
        "health" -> PocketColors.HealthColor
        "education" -> PocketColors.EducationColor
        "travel" -> PocketColors.TravelColor
        else -> PocketColors.OtherColor
    }
}

@Composable
fun getCardTypeColor(cardType: String): Color {
    return when (cardType.lowercase()) {
        "credit" -> PocketColors.CreditCard
        "debit" -> PocketColors.DebitCard
        "prepaid" -> PocketColors.PrepaidCard
        else -> MaterialTheme.colorScheme.primary
    }
}

// Theme state management
@Composable
fun rememberPocketThemeState() = remember {
    PocketThemeState()
}

class PocketThemeState {
    var isDynamicColorEnabled by mutableStateOf(true)
    var isAmoledMode by mutableStateOf(false)
    var accentColor by mutableStateOf(PocketThemeConfig.AccentColor.Purple)
}