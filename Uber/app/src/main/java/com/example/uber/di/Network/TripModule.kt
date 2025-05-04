package com.example.uber.di.Network

import com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service.SocketBroker
import com.example.uber.data.remote.api.backend.rider.socket.trip.repository.TripRepository
import com.example.uber.domain.remote.socket.trip.repository.TripRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TripModule {

    @Provides
    @Singleton
    fun provideTripModule(socket: SocketBroker): TripRepository {
        return TripRepositoryImpl(socket)
    }
}