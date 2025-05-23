package com.example.uber.data.remote.api.backend.rider.socket.ride.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class RideAccepted(
    val riderId :UUID,
    val driverId: UUID,
    val rideId: UUID,
    val latitude: Double,
    val longitude: Double
) : Parcelable