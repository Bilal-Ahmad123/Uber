package com.example.uber.data.remote.api.backend.rider.socket.trip.model

import java.util.UUID

data class DriverReachedDropOffSpot(
    val riderId: UUID,
    val driverId: UUID,
    val rideId: UUID,
    val reached: Boolean
)