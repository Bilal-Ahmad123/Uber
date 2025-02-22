package com.example.uber.di.Network

import com.example.uber.core.common.AppConstants
import com.example.uber.data.remote.api.googleMaps.api.GoogleMapService
import com.example.uber.data.remote.api.googleMaps.repository.IGoogleRepository
import com.example.uber.domain.remote.google.repository.GoogleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GoogleMapsModule {
    @Provides
    @Singleton
    fun provideGoogleMapsGeoCodingService(): GoogleMapService {
        return Retrofit.Builder().baseUrl(AppConstants.GOOGLE_MAPS_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(GoogleMapService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeoCodeRepositoryImpl(api: GoogleMapService): IGoogleRepository {
        return GoogleRepositoryImpl(api)
    }
}