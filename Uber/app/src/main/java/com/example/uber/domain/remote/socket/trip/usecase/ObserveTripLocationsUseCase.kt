package com.example.uber.domain.remote.socket.trip.usecase

import com.example.uber.data.remote.api.backend.rider.socket.ride.repository.RideRepository
import com.example.uber.data.remote.api.backend.rider.socket.trip.repository.TripRepository
import javax.inject.Inject

class ObserveTripLocationsUseCase @Inject constructor(private val rideRepository: TripRepository) {
    operator fun invoke() = rideRepository.observeTrip()
}