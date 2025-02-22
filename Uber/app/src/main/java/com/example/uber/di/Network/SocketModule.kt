package com.example.uber.di.Network

import com.example.uber.data.remote.api.backend.rider.location.api.SocketManager
import com.example.uber.data.remote.api.backend.rider.location.repository.SocketRepository
import com.example.uber.domain.remote.location.repository.SocketRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SocketModule {
    @Provides
    @Singleton
    fun provideSocketRepositoryImpl(socketManager: SocketManager): SocketRepository {
        return SocketRepositoryImpl(socketManager)
    }
}