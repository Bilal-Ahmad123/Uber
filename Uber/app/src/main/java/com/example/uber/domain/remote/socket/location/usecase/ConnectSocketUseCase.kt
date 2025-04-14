package com.example.uber.domain.remote.socket.location.usecase

import com.example.uber.data.remote.api.backend.rider.socket.location.repository.SocketRepository
import okhttp3.WebSocketListener
import javax.inject.Inject

class ConnectSocketUseCase @Inject constructor(private val socketRepository: SocketRepository) {
    operator fun invoke(url: String, listener: WebSocketListener) {
        socketRepository.connect(url, listener)
    }
}