package com.example.uber.domain.remote.location.usecase

import com.example.uber.data.remote.api.backend.rider.location.repository.SocketRepository
import javax.inject.Inject

class StartObservingDriverLocationUseCase @Inject constructor(private val socketRepository: SocketRepository){
    operator fun invoke() {
        socketRepository.startObservingDriverLocationUpdates()
    }
}