package com.example.uber.data.remote.api.backend.rider.socket.ride.repository

import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideRequest

interface RideRepository {
    fun sendRideRequest(rideRequest : RideRequest):Unit
    fun observeRideAcceptedEvent()
}