package com.example.uber.di.DataBase

import android.content.Context
import androidx.room.Room
import com.example.uber.core.common.AppConstants
import com.example.uber.data.local.rider.database.AppDatabase
import com.example.uber.data.local.rider.repository.RiderRoomRepository
import com.example.uberdriver.data.local.driver.dao.RiderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RiderDataBaseModule {
    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context,AppDatabase::class.java,AppConstants.DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun provideDriverDao(appDatabase: AppDatabase) = appDatabase.getRiderDao()

    @Provides
    @Singleton
    fun provideDriverRoomRepository(driverDao:RiderDao):RiderRoomRepository{
        return com.example.uber.domain.local.rider.repository.RiderRoomRepository(driverDao)
    }
}