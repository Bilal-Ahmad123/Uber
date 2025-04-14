package com.example.uber.di.Network

import com.example.uber.domain.remote.socket.SocketRepository.SocketManager
import com.example.uber.data.remote.api.backend.rider.socket.location.repository.LocationRepository
import com.example.uber.domain.remote.socket.location.repository.LocationRepositoryImpl
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
    fun provideSocketRepositoryImpl(socketManager: SocketManager): LocationRepository {
        return LocationRepositoryImpl(socketManager)
    }
}