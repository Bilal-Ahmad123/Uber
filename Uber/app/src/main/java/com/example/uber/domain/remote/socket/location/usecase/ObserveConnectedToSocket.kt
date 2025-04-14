package com.example.uber.domain.remote.socket.location.usecase

import com.example.uber.data.remote.api.backend.rider.socket.location.repository.LocationRepository
import com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service.SocketBroker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectedToSocket @Inject constructor(private val broker: SocketBroker) {
    operator fun invoke():Flow<Boolean>{
        return broker.connectedToSocket()
    }
}