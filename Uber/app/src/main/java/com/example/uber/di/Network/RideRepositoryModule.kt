package com.example.uber.di.Network

import com.example.uber.data.remote.api.backend.rider.socket.ride.repository.RideRepository
import com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service.SocketBroker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RideRepositoryModule {

    @Provides
    @Singleton
    fun provideRideRepositoryImpl(socket : SocketBroker):RideRepository{
        return com.example.uber.domain.remote.socket.ride.repository.RideRepository(socket)
    }
}