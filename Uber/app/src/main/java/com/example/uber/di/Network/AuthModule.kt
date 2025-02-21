package com.example.uber.di.Network

import com.example.uber.core.common.Constants_API
import com.example.uber.data.remote.api.backend.authentication.api.IAuthenticationService
import com.example.uber.data.remote.api.backend.authentication.repository.IAuthRepository
import com.example.uber.domain.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthRepositoryImpl(api: IAuthenticationService): IAuthRepository {
        return AuthRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(): IAuthenticationService {
        return Retrofit.Builder().baseUrl(Constants_API.BACKEND_API)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(IAuthenticationService::class.java)
    }
}