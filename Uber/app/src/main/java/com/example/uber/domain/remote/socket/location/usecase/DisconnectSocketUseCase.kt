package com.example.uber.domain.remote.socket.location.usecase

import com.example.uber.data.remote.api.backend.rider.socket.location.repository.LocationRepository
import javax.inject.Inject

class DisconnectSocketUseCase @Inject constructor(private val socketRepository: LocationRepository) {
    operator fun invoke() {
        socketRepository.disconnect()
    }
}