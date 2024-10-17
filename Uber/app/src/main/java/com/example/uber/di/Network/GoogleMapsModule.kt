package com.example.uber.di.Network

import com.example.uber.app.common.AppConstants
import com.example.uber.data.remote.GeoCode.GoogleMaps.IGeocodingGoogleMapService
import com.example.uber.data.remote.GeoCode.MapBox.IGeocodingService
import com.example.uber.data.repository.IGetGeoCodeGoogleLocationRepository
import com.example.uber.data.repository.IGetGeoCodeLocationRepository
import com.example.uber.domain.repository.GetGeoCodeGoogleLocationRepositoryImpl
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
    fun provideGoogleMapsGeoCodingService(): IGeocodingGoogleMapService {
        return Retrofit.Builder().baseUrl(AppConstants.GOOGLE_MAPS_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(IGeocodingGoogleMapService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeoCodeRepositoryImpl(api: IGeocodingGoogleMapService): IGetGeoCodeGoogleLocationRepository {
        return GetGeoCodeGoogleLocationRepositoryImpl(api)
    }
}