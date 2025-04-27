package com.example.uber.domain.remote.socket.ride.usecase

import com.example.uber.domain.remote.socket.ride.repository.RideRepository
import javax.inject.Inject

class ObserveRideAcceptedUseCase @Inject constructor(private val rideRepository: RideRepository) {
    operator fun invoke() = rideRepository.observeRideAccepted()
}