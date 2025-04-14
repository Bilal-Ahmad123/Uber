package com.example.uber.domain.remote.socket.location.usecase

import com.example.uber.data.remote.api.backend.rider.socket.location.repository.SocketRepository
import com.example.uber.domain.remote.socket.location.model.UpdateLocation
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val socketRepository: SocketRepository) {
    operator fun invoke(location: UpdateLocation) {
        socketRepository.send(location,"UpdateLocation")
    }
}