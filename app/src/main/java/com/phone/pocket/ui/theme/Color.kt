// Enhanced Color.kt - Modern Material 3 Expressive Colors
package com.phone.pocket.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Modern Material 3 Expressive Color Palette
object PocketColors {
    // Primary Colors - Modern Purple-Blue gradient inspired
    val Primary40 = Color(0xFF6750A4)
    val Primary80 = Color(0xFFD0BCFF)
    val Primary90 = Color(0xFFEADDFF)
    val Primary95 = Color(0xFFF6EDFF)
    val Primary10 = Color(0xFF21005D)
    val Primary20 = Color(0xFF381E72)
    val Primary30 = Color(0xFF4F378B)

    // Secondary Colors - Complementary teal-green
    val Secondary40 = Color(0xFF625B71)
    val Secondary80 = Color(0xFFCCC2DC)
    val Secondary90 = Color(0xFFE8DEF8)
    val Secondary95 = Color(0xFFF4EFFA)
    val Secondary10 = Color(0xFF1D192B)
    val Secondary20 = Color(0xFF332D41)
    val Secondary30 = Color(0xFF4A4458)

    // Tertiary Colors - Warm pink accent
    val Tertiary40 = Color(0xFF7D5260)
    val Tertiary80 = Color(0xFFEFB8C8)
    val Tertiary90 = Color(0xFFFFD8E4)
    val Tertiary95 = Color(0xFFFFECF1)
    val Tertiary10 = Color(0xFF31111D)
    val Tertiary20 = Color(0xFF492532)
    val Tertiary30 = Color(0xFF633B48)

    // Error Colors - Modern red system
    val Error40 = Color(0xFFBA1A1A)
    val Error80 = Color(0xFFFFB4AB)
    val Error90 = Color(0xFFFFDAD6)
    val Error95 = Color(0xFFFFEDEA)
    val Error10 = Color(0xFF410E0B)
    val Error20 = Color(0xFF601410)
    val Error30 = Color(0xFF8C1D18)

    // Neutral Colors - Modern gray system
    val Neutral0 = Color(0xFF000000)
    val Neutral10 = Color(0xFF1C1B1F)
    val Neutral20 = Color(0xFF313033)
    val Neutral30 = Color(0xFF484649)
    val Neutral40 = Color(0xFF605D62)
    val Neutral50 = Color(0xFF787579)
    val Neutral60 = Color(0xFF939094)
    val Neutral70 = Color(0xFFAEAAAE)
    val Neutral80 = Color(0xFFCAC4D0)
    val Neutral90 = Color(0xFFE6E0E9)
    val Neutral95 = Color(0xFFF4EFF4)
    val Neutral99 = Color(0xFFFFFBFE)
    val Neutral100 = Color(0xFFFFFFFF)

    // Surface Colors - Enhanced hierarchy
    val Surface = Color(0xFFFEF7FF)
    val SurfaceDim = Color(0xFFDED8E1)
    val SurfaceBright = Color(0xFFFEF7FF)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF7F2FA)
    val SurfaceContainer = Color(0xFFF1ECF4)
    val SurfaceContainerHigh = Color(0xFFECE6F0)
    val SurfaceContainerHighest = Color(0xFFE6E0E9)

    // Inverse Colors
    val InverseSurface = Color(0xFF322F35)
    val InverseOnSurface = Color(0xFFF5EFF7)
    val InversePrimary = Color(0xFFD0BCFF)

    // Outline Colors
    val Outline = Color(0xFF79747E)
    val OutlineVariant = Color(0xFFCAC4D0)

    // Special Colors for Financial App
    val Success = Color(0xFF00C853)
    val Warning = Color(0xFFFF9800)
    val Info = Color(0xFF2196F3)
    val Expense = Color(0xFFE91E63)
    val Income = Color(0xFF4CAF50)

    // Card Type Colors
    val CreditCard = Color(0xFF1976D2)
    val DebitCard = Color(0xFF388E3C)
    val PrepaidCard = Color(0xFF7B1FA2)

    // Category Colors (Modern vibrant palette)
    val FoodColor = Color(0xFFFF6B6B)
    val TransportColor = Color(0xFF4ECDC4)
    val ShoppingColor = Color(0xFFFFE66D)
    val EntertainmentColor = Color(0xFF95E1D3)
    val BillsColor = Color(0xFFFCA3CC)
    val HealthColor = Color(0xFFA8E6CF)
    val EducationColor = Color(0xFFFFD93D)
    val TravelColor = Color(0xFF6C5CE7)
    val OtherColor = Color(0xFFA0A0A0)
}

// Light Theme Configuration
val LightColorScheme = lightColorScheme(
    primary = PocketColors.Primary40,
    onPrimary = PocketColors.Neutral100,
    primaryContainer = PocketColors.Primary90,
    onPrimaryContainer = PocketColors.Primary10,

    secondary = PocketColors.Secondary40,
    onSecondary = PocketColors.Neutral100,
    secondaryContainer = PocketColors.Secondary90,
    onSecondaryContainer = PocketColors.Secondary10,

    tertiary = PocketColors.Tertiary40,
    onTertiary = PocketColors.Neutral100,
    tertiaryContainer = PocketColors.Tertiary90,
    onTertiaryContainer = PocketColors.Tertiary10,

    error = PocketColors.Error40,
    onError = PocketColors.Neutral100,
    errorContainer = PocketColors.Error90,
    onErrorContainer = PocketColors.Error10,

    background = PocketColors.Surface,
    onBackground = PocketColors.Neutral10,

    surface = PocketColors.Surface,
    onSurface = PocketColors.Neutral10,
    surfaceVariant = PocketColors.Neutral90,
    onSurfaceVariant = PocketColors.Neutral30,

    surfaceDim = PocketColors.SurfaceDim,
    surfaceBright = PocketColors.SurfaceBright,
    surfaceContainerLowest = PocketColors.SurfaceContainerLowest,
    surfaceContainerLow = PocketColors.SurfaceContainerLow,
    surfaceContainer = PocketColors.SurfaceContainer,
    surfaceContainerHigh = PocketColors.SurfaceContainerHigh,
    surfaceContainerHighest = PocketColors.SurfaceContainerHighest,

    inverseSurface = PocketColors.InverseSurface,
    inverseOnSurface = PocketColors.InverseOnSurface,
    inversePrimary = PocketColors.InversePrimary,

    outline = PocketColors.Outline,
    outlineVariant = PocketColors.OutlineVariant,

    scrim = PocketColors.Neutral0
)

// Dark Theme Configuration
val DarkColorScheme = darkColorScheme(
    primary = PocketColors.Primary80,
    onPrimary = PocketColors.Primary20,
    primaryContainer = PocketColors.Primary30,
    onPrimaryContainer = PocketColors.Primary90,

    secondary = PocketColors.Secondary80,
    onSecondary = PocketColors.Secondary20,
    secondaryContainer = PocketColors.Secondary30,
    onSecondaryContainer = PocketColors.Secondary90,

    tertiary = PocketColors.Tertiary80,
    onTertiary = PocketColors.Tertiary20,
    tertiaryContainer = PocketColors.Tertiary30,
    onTertiaryContainer = PocketColors.Tertiary90,

    error = PocketColors.Error80,
    onError = PocketColors.Error20,
    errorContainer = PocketColors.Error30,
    onErrorContainer = PocketColors.Error90,

    background = PocketColors.Neutral10,
    onBackground = PocketColors.Neutral90,

    surface = PocketColors.Neutral10,
    onSurface = PocketColors.Neutral90,
    surfaceVariant = PocketColors.Neutral30,
    onSurfaceVariant = PocketColors.Neutral80,

    surfaceDim = PocketColors.Neutral6,
    surfaceBright = PocketColors.Neutral24,
    surfaceContainerLowest = PocketColors.Neutral4,
    surfaceContainerLow = PocketColors.Neutral10,
    surfaceContainer = PocketColors.Neutral12,
    surfaceContainerHigh = PocketColors.Neutral17,
    surfaceContainerHighest = PocketColors.Neutral22,

    inverseSurface = PocketColors.Neutral90,
    inverseOnSurface = PocketColors.Neutral20,
    inversePrimary = PocketColors.Primary40,

    outline = PocketColors.Neutral60,
    outlineVariant = PocketColors.Neutral30,

    scrim = PocketColors.Neutral0
)

// Extension for dark theme surface colors
val PocketColors.Neutral4 get() = Color(0xFF0F0D13)
val PocketColors.Neutral6 get() = Color(0xFF141218)
val PocketColors.Neutral12 get() = Color(0xFF201F23)
val PocketColors.Neutral17 get() = Color(0xFF2B2930)
val PocketColors.Neutral22 get() = Color(0xFF36343B)
val PocketColors.Neutral24 get() = Color(0xFF3B383E)