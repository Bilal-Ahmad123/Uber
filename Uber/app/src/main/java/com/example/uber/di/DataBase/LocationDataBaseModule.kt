package com.example.uber.di.DataBase

import android.content.Context
import androidx.room.Room
import com.example.uber.data.local.location.dao.LocationDao
import com.example.uber.data.local.location.dataBase.AppDatabase
import com.example.uber.data.local.location.repository.LocationRepository
import com.example.uber.domain.local.location.repository.LocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocationDataBaseModule {

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
    fun provideLocationRepositoryImpl(database: LocationDao): LocationRepository {
        return LocationRepositoryImpl(database)
    }

}