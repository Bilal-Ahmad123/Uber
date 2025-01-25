package com.example.uber.di.Network

import com.example.uber.data.remote.socket.SocketManager
import com.example.uber.data.repository.ISocketRepository
import com.example.uber.domain.repository.SocketRepositoryImpl
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
    fun provideSocketRepositoryImpl(socketManager: SocketManager):ISocketRepository{
        return SocketRepositoryImpl(socketManager)
    }
}