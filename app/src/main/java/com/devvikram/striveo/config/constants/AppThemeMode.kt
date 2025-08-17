package com.devvikram.striveo.config.constants

enum class AppThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
    STRIVEO_BLUE,
    STRIVEO_PURPLE,
    STRIVEO_GREEN;

    companion object {
        fun fromString(value: String): AppThemeMode =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
    }
}
