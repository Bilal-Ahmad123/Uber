package com.example.uber.di.Network

import com.example.uber.core.common.AppConstants
import com.example.uber.core.common.Constants_API
import com.example.uber.data.remote.api.backend.rider.general.api.RiderService
import com.example.uber.data.remote.api.backend.rider.general.repository.RiderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RiderModule {

    @Provides
    @Singleton
    fun provideRiderService() : RiderService{
        return Retrofit.Builder().baseUrl(Constants_API.BACKEND_RIDER_API)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(RiderService::class.java)
    }

    @Provides
    @Singleton
    fun provideRiderRepository(riderService: RiderService):RiderRepository{
        return com.example.uber.domain.remote.general.repository.RiderRepository(riderService)
    }
}