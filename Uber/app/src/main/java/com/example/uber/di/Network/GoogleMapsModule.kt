package com.example.uber.di.Network

import com.example.uber.core.common.AppConstants
import com.example.uber.data.remote.api.GoogleMaps.IGoogleMapService
import com.example.uber.data.repository.IGoogleRepository
import com.example.uber.domain.repository.GoogleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleMapsModule {
    @Provides
    @Singleton
    fun provideGoogleMapsGeoCodingService(): IGoogleMapService {
        return Retrofit.Builder().baseUrl(AppConstants.GOOGLE_MAPS_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(IGoogleMapService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeoCodeRepositoryImpl(api: IGoogleMapService): IGoogleRepository {
        return GoogleRepositoryImpl(api)
    }
}