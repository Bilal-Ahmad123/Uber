package com.example.uber.di.DataBase

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.uber.data.local.dao.LocationDao
import com.example.uber.data.local.dataBase.AppDatabase
import com.example.uber.data.repository.ILocationRepository
import com.example.uber.domain.repository.LocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "uber_database").build()
    }

    @Provides
    @Singleton
    fun currentLocationDao(database: AppDatabase) = database.currentLocationDao()

    @Provides
    @Singleton
    fun provideLocationRepositoryImpl(database: LocationDao): ILocationRepository {
        return LocationRepositoryImpl(database)
    }

}