package com.example.uber.domain.remote.socket.ride.usecase

import com.example.uber.domain.remote.socket.ride.repository.RideRepository
import javax.inject.Inject

class StartObservingRideAcceptedUseCase @Inject constructor(private val rideRepository: com.example.uber.data.remote.api.backend.rider.socket.ride.repository.RideRepository){
    operator fun invoke() = rideRepository.startObservingRideAcceptedEvent()
}