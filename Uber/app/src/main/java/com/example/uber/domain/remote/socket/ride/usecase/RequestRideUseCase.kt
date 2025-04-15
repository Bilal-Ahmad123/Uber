package com.example.uber.domain.remote.socket.ride.usecase

import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideRequest
import com.example.uber.data.remote.api.backend.rider.socket.ride.repository.RideRepository
import javax.inject.Inject

class RequestRideUseCase @Inject constructor(private val rideRepository: RideRepository) {
     operator fun invoke(rideRequest: RideRequest){
        rideRepository.sendRideRequest(rideRequest)
    }
}