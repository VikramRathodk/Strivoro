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

