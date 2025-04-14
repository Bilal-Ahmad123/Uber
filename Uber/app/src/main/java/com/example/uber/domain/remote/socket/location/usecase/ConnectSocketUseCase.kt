package com.example.uber.domain.remote.socket.location.usecase

import com.example.uber.data.remote.api.backend.rider.socket.location.repository.LocationRepository
import com.example.uber.data.remote.api.backend.rider.socket.socketBroker.service.SocketBroker
import okhttp3.WebSocketListener
import javax.inject.Inject

class ConnectSocketUseCase @Inject constructor(private val broker: SocketBroker) {
    operator fun invoke(url: String) {
        broker.connect(url)
    }
}