package com.example.uber.di.Network

import com.example.uber.app.common.AppConstants
import com.example.uber.data.remote.GeoCode.MapBox.IGeocodingService
import com.example.uber.data.repository.IGetGeoCodeLocationRepository
import com.example.uber.domain.repository.GetGeoCodeLocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapBoxNetwork {

    @Provides
    @Singleton
    fun provideMapBoxGeoCodingService(): IGeocodingService {
        return Retrofit.Builder().baseUrl(AppConstants.MAP_BOX_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(IGeocodingService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeoCodeRepositoryImpl(api: IGeocodingService):IGetGeoCodeLocationRepository{
        return GetGeoCodeLocationRepositoryImpl(api)
    }
}