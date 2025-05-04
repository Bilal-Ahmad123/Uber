package com.example.uber.data.remote.api.backend.rider.socket.ride.model

import java.util.UUID

data class TripLocation(
    val rideId: UUID,
    val driverId: UUID,
    val latitude: Double,
    val longitude: Double,
    val time:Int,
    val distance: Int
)