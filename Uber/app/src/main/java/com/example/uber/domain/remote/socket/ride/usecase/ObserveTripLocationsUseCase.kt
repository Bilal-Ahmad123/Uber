package com.example.uber.domain.remote.socket.ride.usecase

import com.example.uber.data.remote.api.backend.rider.socket.ride.repository.RideRepository
import javax.inject.Inject

class ObserveTripLocationsUseCase @Inject constructor(private val rideRepository: RideRepository) {
    operator fun invoke() = rideRepository.observeTrip()
}