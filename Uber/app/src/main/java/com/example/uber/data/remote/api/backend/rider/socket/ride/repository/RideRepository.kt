package com.example.uber.data.remote.api.backend.rider.socket.ride.repository

import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideAccepted
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideRequest
import kotlinx.coroutines.flow.Flow

interface RideRepository {
    fun sendRideRequest(rideRequest : RideRequest):Unit
    fun startObservingRideAcceptedEvent():Flow<RideAccepted>
    fun observeRideAccepted():Flow<RideAccepted>
}