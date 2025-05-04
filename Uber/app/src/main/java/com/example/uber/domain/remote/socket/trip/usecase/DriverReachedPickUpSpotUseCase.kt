package com.example.uber.domain.remote.socket.trip.usecase

import com.example.uber.data.remote.api.backend.rider.socket.trip.repository.TripRepository
import javax.inject.Inject

class DriverReachedPickUpSpotUseCase @Inject constructor(private val repository: TripRepository) {
    suspend operator fun invoke() = repository.driverReachedPickUpSpot()
}