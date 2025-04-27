package com.example.uber.domain.remote.socket.ride.usecase

import com.example.uber.domain.remote.socket.ride.repository.RideRepository
import javax.inject.Inject

class StartObservingRideAcceptedUseCase @Inject constructor(private val rideRepository: RideRepository){
    operator fun invoke() = rideRepository.startObservingRideAcceptedEvent()
}