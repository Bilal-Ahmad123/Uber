package com.example.uber.data.remote.api.backend.rider.socket.ride.model

import java.util.UUID

data class RideAccepted(
    val riderId :UUID,
    val driverId: UUID,
    val rideId: UUID,
    val latitude: Double,
    val longitude: Double
)