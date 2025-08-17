package com.devvikram.striveo.room.converters

import androidx.room.TypeConverter
import com.devvikram.striveo.config.constants.AppThemeMode
import com.devvikram.striveo.config.constants.PreferenceLanguage
import com.devvikram.striveo.config.constants.UserAccountType

class Converters {

    @TypeConverter
    fun fromUserAccountType(type: UserAccountType): String = type.name

    @TypeConverter
    fun toUserAccountType(value: String): UserAccountType =
        UserAccountType.fromString(value)


    @TypeConverter
    fun fromThemeMode(mode: AppThemeMode): String = mode.name

    @TypeConverter
    fun toThemeMode(value: String): AppThemeMode =
        AppThemeMode.fromString(value)


    @TypeConverter
    fun fromLanguage(lang: PreferenceLanguage): String = lang.code

    @TypeConverter
    fun toLanguage(code: String): PreferenceLanguage = PreferenceLanguage.fromCode(code)

    @TypeConverter
    fun fromTags(tags: List<String>): String = tags.joinToString(",")

    @TypeConverter
    fun toTags(value: String): List<String> = value.split(",")


}