package com.example.uber.di.DataBase

import com.example.uber.data.local.Dao.PickUpLocationDao
import com.example.uber.data.remote.GeoCode.GoogleMaps.IGeocodingGoogleMapService
import com.example.uber.data.repository.IPickUpLocationRepository
import com.example.uber.domain.repository.PickUpLocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PickUpLocationModule {
    @Provides
    @Singleton
    fun providePickUpLocationRepository(pickUpLocationDao: PickUpLocationDao,googleApi: IGeocodingGoogleMapService): IPickUpLocationRepository {
        return PickUpLocationRepositoryImpl(pickUpLocationDao,googleApi)
    }

}