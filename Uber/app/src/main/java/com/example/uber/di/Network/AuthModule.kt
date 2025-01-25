package com.example.uber.di.Network

import com.example.uber.data.repository.IAuthRepository
import com.example.uber.domain.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthRepositoryImpl(): IAuthRepository {
        return AuthRepositoryImpl()
    }
}