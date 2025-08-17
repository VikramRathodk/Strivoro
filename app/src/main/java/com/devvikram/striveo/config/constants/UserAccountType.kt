package com.devvikram.striveo.config.constants

enum class UserAccountType {
    PERSONAL,
    ORGANIZATION,
    EXTERNAL,
    TEAM,
    ENTERPRISE,
    INVITED;


    companion object {
        fun fromString(value: String): UserAccountType = entries
            .firstOrNull { it.name.equals(value, ignoreCase = true) }
            ?: PERSONAL
    }
}
