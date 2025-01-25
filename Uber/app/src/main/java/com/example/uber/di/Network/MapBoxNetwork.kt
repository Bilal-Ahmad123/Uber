package com.example.uber.di.Network

import com.example.uber.core.common.AppConstants
import com.example.uber.data.remote.api.MapBox.IMapboxService
import com.example.uber.data.repository.IMapBoxRepository
import com.example.uber.domain.repository.MapboxRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MapBoxNetwork {

    @Provides
    @Singleton
    fun provideMapBoxGeoCodingService(): IMapboxService {
        return Retrofit.Builder().baseUrl(AppConstants.MAP_BOX_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(IMapboxService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeoCodeRepositoryImpl(api: IMapboxService):IMapBoxRepository{
        return MapboxRepositoryImpl(api)
    }
}