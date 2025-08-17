// Typography.kt
package com.devvikram.striveo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.devvikram.striveo.R

// Google Fonts provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Define Google Font families
val interGoogleFont = GoogleFont("Inter")
val poppinsGoogleFont = GoogleFont("Poppins")
val robotoGoogleFont = GoogleFont("Roboto")

// Create FontFamily instances
val InterFontFamily = FontFamily(
    Font(googleFont = interGoogleFont, fontProvider = provider),
    Font(googleFont = interGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = interGoogleFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = interGoogleFont, fontProvider = provider, weight = FontWeight.Bold)
)

val PoppinsFontFamily = FontFamily(
    Font(googleFont = poppinsGoogleFont, fontProvider = provider),
    Font(googleFont = poppinsGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = poppinsGoogleFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = poppinsGoogleFont, fontProvider = provider, weight = FontWeight.Bold)
)

val RobotoFontFamily = FontFamily(
    Font(googleFont = robotoGoogleFont, fontProvider = provider),
    Font(googleFont = robotoGoogleFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = robotoGoogleFont, fontProvider = provider, weight = FontWeight.Bold)
)

// Use Inter for body text and Poppins for headings
val StriveoFontFamily = InterFontFamily
val StriveoDisplayFontFamily = PoppinsFontFamily

// Complete Material 3 Typography System
val StriveoTypography = Typography(
    // Display styles - For large, prominent text
    displayLarge = TextStyle(
        fontFamily = StriveoDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = StriveoDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = StriveoDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline styles - For prominent headings
    headlineLarge = TextStyle(
        fontFamily = StriveoDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = StriveoDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = StriveoDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Title styles - For medium-emphasis headings
    titleLarge = TextStyle(
        fontFamily = StriveoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    )
)
