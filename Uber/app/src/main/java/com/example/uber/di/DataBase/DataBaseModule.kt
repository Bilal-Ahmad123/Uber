package com.example.uber.di.DataBase

import android.content.Context
import androidx.room.Room
import com.example.uber.data.local.DataBase.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "uber_database").build()
    }

    @Provides
    @Singleton
    fun providePickUpLocationDao(database: AppDatabase) = database.pickUpLocationDao()

    @Provides
    @Singleton
    fun provideDropOffLocationDao(database: AppDatabase) = database.dropOffLocationDao()
}