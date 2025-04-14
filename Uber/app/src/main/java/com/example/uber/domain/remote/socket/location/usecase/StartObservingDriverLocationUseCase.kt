package com.example.uber.domain.remote.socket.location.usecase

import com.example.uber.data.remote.api.backend.rider.socket.location.repository.LocationRepository
import javax.inject.Inject

class StartObservingDriverLocationUseCase @Inject constructor(private val locationRepository: LocationRepository){
    operator fun invoke() {
        locationRepository.startObservingDriverLocationUpdates()
    }
}