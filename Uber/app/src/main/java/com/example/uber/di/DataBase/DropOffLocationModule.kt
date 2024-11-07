package com.example.uber.di.DataBase

import com.example.uber.data.repository.IDropOffLocationRepository
import com.example.uber.domain.repository.DropOffLocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.uber.data.local.Dao.DropOffLocationDao
import com.example.uber.data.remote.api.GoogleMaps.IGoogleMapService

@Module
@InstallIn(SingletonComponent::class)
object DropOffLocationModule {
    @Singleton
    @Provides
    fun provideDropOffLocationRepositoryImpl(dropOffLocationDao:DropOffLocationDao,googleApi: IGoogleMapService):IDropOffLocationRepository{
        return DropOffLocationRepositoryImpl(dropOffLocationDao,googleApi)
    }
}