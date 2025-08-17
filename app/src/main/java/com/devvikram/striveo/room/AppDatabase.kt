package com.devvikram.striveo.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devvikram.striveo.room.model.RoomUser
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.room.converters.Converters
import com.devvikram.striveo.room.dao.RoomUserDao
import com.devvikram.striveo.room.dao.TaskDao
import com.devvikram.striveo.room.model.RoomTask


@Database(
    entities = [RoomUser::class,
        RoomTask::class],
    version = App.DATABASE_CURRENT_VERSION,
    exportSchema = true
)
@AutoMigration(from = App.DATABASE_LAST_VERSION, to = App.DATABASE_CURRENT_VERSION)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomUserDao(): RoomUserDao
    abstract fun taskDao(): TaskDao


}