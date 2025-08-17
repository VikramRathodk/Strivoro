package com.devvikram.striveo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.devvikram.striveo.config.constants.AppThemeMode

private val DefaultLightColorScheme = lightColorScheme(
    primary = DefaultLight_Primary,
    onPrimary = DefaultLight_OnPrimary,
    primaryContainer = DefaultLight_PrimaryContainer,
    onPrimaryContainer = DefaultLight_OnPrimaryContainer,

    secondary = DefaultLight_Secondary,
    onSecondary = DefaultLight_OnSecondary,
    secondaryContainer = DefaultLight_SecondaryContainer,
    onSecondaryContainer = DefaultLight_OnSecondaryContainer,

    tertiary = DefaultLight_Tertiary,
    onTertiary = DefaultLight_OnTertiary,
    tertiaryContainer = DefaultLight_TertiaryContainer,
    onTertiaryContainer = DefaultLight_OnTertiaryContainer,

    error = DefaultLight_Error,
    onError = DefaultLight_OnError,
    errorContainer = DefaultLight_ErrorContainer,
    onErrorContainer = DefaultLight_OnErrorContainer,

    background = DefaultLight_Background,
    onBackground = DefaultLight_OnBackground,
    surface = DefaultLight_Surface,
    onSurface = DefaultLight_OnSurface,

    surfaceVariant = DefaultLight_SurfaceVariant,
    onSurfaceVariant = DefaultLight_OnSurfaceVariant,
    outline = DefaultLight_Outline,

    inverseOnSurface = DefaultLight_InverseOnSurface,
    inverseSurface = DefaultLight_InverseSurface,
    inversePrimary = DefaultLight_InversePrimary
)


private val DefaultDarkColorScheme = darkColorScheme(
    primary = DefaultDark_Primary,
    onPrimary = DefaultDark_OnPrimary,
    primaryContainer = DefaultDark_PrimaryContainer,
    onPrimaryContainer = DefaultDark_OnPrimaryContainer,

    secondary = DefaultDark_Secondary,
    onSecondary = DefaultDark_OnSecondary,
    secondaryContainer = DefaultDark_SecondaryContainer,
    onSecondaryContainer = DefaultDark_OnSecondaryContainer,

    tertiary = DefaultDark_Tertiary,
    onTertiary = DefaultDark_OnTertiary,
    tertiaryContainer = DefaultDark_TertiaryContainer,
    onTertiaryContainer = DefaultDark_OnTertiaryContainer,

    error = DefaultDark_Error,
    onError = DefaultDark_OnError,
    errorContainer = DefaultDark_ErrorContainer,
    onErrorContainer = DefaultDark_OnErrorContainer,

    background = DefaultDark_Background,
    onBackground = DefaultDark_OnBackground,
    surface = DefaultDark_Surface,
    onSurface = DefaultDark_OnSurface,

    surfaceVariant = DefaultDark_SurfaceVariant,
    onSurfaceVariant = DefaultDark_OnSurfaceVariant,
    outline = DefaultDark_Outline,

    inverseOnSurface = DefaultDark_InverseOnSurface,
    inverseSurface = DefaultDark_InverseSurface,
    inversePrimary = DefaultDark_InversePrimary
)

// Striveo Blue Color Schemes
private val StriveoBlueLight = lightColorScheme(
    primary = BlueLight_Primary,
    onPrimary = BlueLight_OnPrimary,
    primaryContainer = BlueLight_PrimaryContainer,
    onPrimaryContainer = BlueLight_OnPrimaryContainer,
    secondary = BlueLight_Secondary,
    onSecondary = BlueLight_OnSecondary,
    secondaryContainer = BlueLight_SecondaryContainer,
    onSecondaryContainer = BlueLight_OnSecondaryContainer,
    tertiary = BlueLight_Tertiary,
    onTertiary = BlueLight_OnTertiary,
    tertiaryContainer = BlueLight_TertiaryContainer,
    onTertiaryContainer = BlueLight_OnTertiaryContainer,
    error = BlueLight_Error,
    errorContainer = BlueLight_ErrorContainer,
    onError = BlueLight_OnError,
    onErrorContainer = BlueLight_OnErrorContainer,
    background = BlueLight_Background,
    onBackground = BlueLight_OnBackground,
    surface = BlueLight_Surface,
    onSurface = BlueLight_OnSurface,
    surfaceVariant = BlueLight_SurfaceVariant,
    onSurfaceVariant = BlueLight_OnSurfaceVariant,
    outline = BlueLight_Outline,
    inverseOnSurface = BlueLight_InverseOnSurface,
    inverseSurface = BlueLight_InverseSurface,
    inversePrimary = BlueLight_InversePrimary,
)

private val StriveoBlueDark = darkColorScheme(
    primary = BlueDark_Primary,
    onPrimary = BlueDark_OnPrimary,
    primaryContainer = BlueDark_PrimaryContainer,
    onPrimaryContainer = BlueDark_OnPrimaryContainer,
    secondary = BlueDark_Secondary,
    onSecondary = BlueDark_OnSecondary,
    secondaryContainer = BlueDark_SecondaryContainer,
    onSecondaryContainer = BlueDark_OnSecondaryContainer,
    tertiary = BlueDark_Tertiary,
    onTertiary = BlueDark_OnTertiary,
    tertiaryContainer = BlueDark_TertiaryContainer,
    onTertiaryContainer = BlueDark_OnTertiaryContainer,
    error = BlueDark_Error,
    errorContainer = BlueDark_ErrorContainer,
    onError = BlueDark_OnError,
    onErrorContainer = BlueDark_OnErrorContainer,
    background = BlueDark_Background,
    onBackground = BlueDark_OnBackground,
    surface = BlueDark_Surface,
    onSurface = BlueDark_OnSurface,
    surfaceVariant = BlueDark_SurfaceVariant,
    onSurfaceVariant = BlueDark_OnSurfaceVariant,
    outline = BlueDark_Outline,
    inverseOnSurface = BlueDark_InverseOnSurface,
    inverseSurface = BlueDark_InverseSurface,
    inversePrimary = BlueDark_InversePrimary,
)

// Striveo Purple Color Schemes
private val StriveoPurpleLight = lightColorScheme(
    primary = PurpleLight_Primary,
    onPrimary = PurpleLight_OnPrimary,
    primaryContainer = PurpleLight_PrimaryContainer,
    onPrimaryContainer = PurpleLight_OnPrimaryContainer,
    secondary = PurpleLight_Secondary,
    onSecondary = PurpleLight_OnSecondary,
    secondaryContainer = PurpleLight_SecondaryContainer,
    onSecondaryContainer = PurpleLight_OnSecondaryContainer,
    tertiary = PurpleLight_Tertiary,
    onTertiary = PurpleLight_OnTertiary,
    tertiaryContainer = PurpleLight_TertiaryContainer,
    onTertiaryContainer = PurpleLight_OnTertiaryContainer,
    error = PurpleLight_Error,
    errorContainer = PurpleLight_ErrorContainer,
    onError = PurpleLight_OnError,
    onErrorContainer = PurpleLight_OnErrorContainer,
    background = PurpleLight_Background,
    onBackground = PurpleLight_OnBackground,
    surface = PurpleLight_Surface,
    onSurface = PurpleLight_OnSurface,
    surfaceVariant = PurpleLight_SurfaceVariant,
    onSurfaceVariant = PurpleLight_OnSurfaceVariant,
    outline = PurpleLight_Outline,
    inverseOnSurface = PurpleLight_InverseOnSurface,
    inverseSurface = PurpleLight_InverseSurface,
    inversePrimary = PurpleLight_InversePrimary,
)

private val StriveoPurpleDark = darkColorScheme(
    primary = PurpleDark_Primary,
    onPrimary = PurpleDark_OnPrimary,
    primaryContainer = PurpleDark_PrimaryContainer,
    onPrimaryContainer = PurpleDark_OnPrimaryContainer,
    secondary = PurpleDark_Secondary,
    onSecondary = PurpleDark_OnSecondary,
    secondaryContainer = PurpleDark_SecondaryContainer,
    onSecondaryContainer = PurpleDark_OnSecondaryContainer,
    tertiary = PurpleDark_Tertiary,
    onTertiary = PurpleDark_OnTertiary,
    tertiaryContainer = PurpleDark_TertiaryContainer,
    onTertiaryContainer = PurpleDark_OnTertiaryContainer,
    error = PurpleDark_Error,
    errorContainer = PurpleDark_ErrorContainer,
    onError = PurpleDark_OnError,
    onErrorContainer = PurpleDark_OnErrorContainer,
    background = PurpleDark_Background,
    onBackground = PurpleDark_OnBackground,
    surface = PurpleDark_Surface,
    onSurface = PurpleDark_OnSurface,
    surfaceVariant = PurpleDark_SurfaceVariant,
    onSurfaceVariant = PurpleDark_OnSurfaceVariant,
    outline = PurpleDark_Outline,
    inverseOnSurface = PurpleDark_InverseOnSurface,
    inverseSurface = PurpleDark_InverseSurface,
    inversePrimary = PurpleDark_InversePrimary,
)

// Striveo Green Color Schemes
private val StriveoGreenLight = lightColorScheme(
    primary = GreenLight_Primary,
    onPrimary = GreenLight_OnPrimary,
    primaryContainer = GreenLight_PrimaryContainer,
    onPrimaryContainer = GreenLight_OnPrimaryContainer,
    secondary = GreenLight_Secondary,
    onSecondary = GreenLight_OnSecondary,
    secondaryContainer = GreenLight_SecondaryContainer,
    onSecondaryContainer = GreenLight_OnSecondaryContainer,
    tertiary = GreenLight_Tertiary,
    onTertiary = GreenLight_OnTertiary,
    tertiaryContainer = GreenLight_TertiaryContainer,
    onTertiaryContainer = GreenLight_OnTertiaryContainer,
    error = GreenLight_Error,
    errorContainer = GreenLight_ErrorContainer,
    onError = GreenLight_OnError,
    onErrorContainer = GreenLight_OnErrorContainer,
    background = GreenLight_Background,
    onBackground = GreenLight_OnBackground,
    surface = GreenLight_Surface,
    onSurface = GreenLight_OnSurface,
    surfaceVariant = GreenLight_SurfaceVariant,
    onSurfaceVariant = GreenLight_OnSurfaceVariant,
    outline = GreenLight_Outline,
    inverseOnSurface = GreenLight_InverseOnSurface,
    inverseSurface = GreenLight_InverseSurface,
    inversePrimary = GreenLight_InversePrimary,
)

private val StriveoGreenDark = darkColorScheme(
    primary = GreenDark_Primary,
    onPrimary = GreenDark_OnPrimary,
    primaryContainer = GreenDark_PrimaryContainer,
    onPrimaryContainer = GreenDark_OnPrimaryContainer,
    secondary = GreenDark_Secondary,
    onSecondary = GreenDark_OnSecondary,
    secondaryContainer = GreenDark_SecondaryContainer,
    onSecondaryContainer = GreenDark_OnSecondaryContainer,
    tertiary = GreenDark_Tertiary,
    onTertiary = GreenDark_OnTertiary,
    tertiaryContainer = GreenDark_TertiaryContainer,
    onTertiaryContainer = GreenDark_OnTertiaryContainer,
    error = GreenDark_Error,
    errorContainer = GreenDark_ErrorContainer,
    onError = GreenDark_OnError,
    onErrorContainer = GreenDark_OnErrorContainer,
    background = GreenDark_Background,
    onBackground = GreenDark_OnBackground,
    surface = GreenDark_Surface,
    onSurface = GreenDark_OnSurface,
    surfaceVariant = GreenDark_SurfaceVariant,
    onSurfaceVariant = GreenDark_OnSurfaceVariant,
    outline = GreenDark_Outline,
    inverseOnSurface = GreenDark_InverseOnSurface,
    inverseSurface = GreenDark_InverseSurface,
    inversePrimary = GreenDark_InversePrimary,
)

@Composable
fun StriveoTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkThemeSystem = isSystemInDarkTheme()
    val isDarkTheme = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> darkThemeSystem
        else -> darkThemeSystem
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && themeMode == AppThemeMode.SYSTEM -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        themeMode == AppThemeMode.STRIVEO_BLUE -> {
            if (isDarkTheme) StriveoBlueDark else StriveoBlueLight
        }

        themeMode == AppThemeMode.STRIVEO_PURPLE -> {
            if (isDarkTheme) StriveoPurpleDark else StriveoPurpleLight
        }

        themeMode == AppThemeMode.STRIVEO_GREEN -> {
            if (isDarkTheme) StriveoGreenDark else StriveoGreenLight
        }

        isDarkTheme -> DefaultDarkColorScheme
        else -> DefaultLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = StriveoTypography,
        content = content
    )
}

// Utility function to get theme display name
fun AppThemeMode.getDisplayName(): String = when (this) {
    AppThemeMode.LIGHT -> "Light"
    AppThemeMode.DARK -> "Dark"
    AppThemeMode.SYSTEM -> "System"
    AppThemeMode.STRIVEO_BLUE -> "Striveo Blue"
    AppThemeMode.STRIVEO_PURPLE -> "Striveo Purple"
    AppThemeMode.STRIVEO_GREEN -> "Striveo Green"
}
