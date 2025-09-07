package com.devvikram.striveo.config.modules

import android.content.Context
import androidx.room.Room
import com.devvikram.striveo.config.constants.App
import com.devvikram.striveo.config.constants.LoginPreference
import com.devvikram.striveo.firebase.repository.FirebaseProjectRepository
import com.devvikram.striveo.firebase.repository.FirebaseTaskRepository
import com.devvikram.striveo.firebase.repository.FirebaseUserRepository
import com.devvikram.striveo.room.AppDatabase
import com.devvikram.striveo.room.dao.RoomProjectDao
import com.devvikram.striveo.room.dao.RoomUserDao
import com.devvikram.striveo.room.dao.TaskDao
import com.devvikram.striveo.room.repository.RoomProjectRepository
import com.devvikram.striveo.room.repository.RoomTaskRepository
import com.devvikram.striveo.room.repository.RoomUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    // Firebase Authentication and Firestore
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides

    fun provideFireAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }


    @Provides
    fun provideLoginPreference(@ApplicationContext context: Context): LoginPreference {
        return LoginPreference(context)
    }



    // Room Database
    @Provides
    fun provideDatabase(@ApplicationContext app: Context): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            App.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    // user dao and repo

    @Provides
    fun provideUserDao(db: AppDatabase) = db.roomUserDao()


    @Provides
    fun provideUserRepository(userDao: RoomUserDao) = RoomUserRepository(userDao)

    @Provides
    fun provideFirebaseUserRepository(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore) =
        FirebaseUserRepository(firebaseAuth, firestore)


    @Provides
    fun provideTaskDao(db: AppDatabase) = db.taskDao()


    @Provides
    fun provideRoomTaskRepository(taskDao: TaskDao) = RoomTaskRepository(taskDao)

    @Provides
    fun provideFirebaseTaskRepository(firestore: FirebaseFirestore) = FirebaseTaskRepository(
        firebaseFirestore = firestore
    )

    @Provides
    fun provideRoomProjectDao(db: AppDatabase) = db.roomProjectDao()

    @Provides
    fun provideRoomProjectRepository(roomProjectDao: RoomProjectDao) = RoomProjectRepository(
        roomProjectDao = roomProjectDao
    )

    @Provides
    fun provideFirebaseProjectRepository(firestore: FirebaseFirestore) = FirebaseProjectRepository(
        firebaseFirestore = firestore
    )


}