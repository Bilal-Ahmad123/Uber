package com.example.uber.data.remote.api.backend.rider.location.mapper

import com.example.uber.data.remote.api.backend.rider.location.model.UpdateDriverLocationResponse
import java.util.UUID

fun UpdateDriverLocationResponse.toDomain():UpdateDriverLocation{
    return UpdateDriverLocation(userId, longitude, latitude,vehicleType)
}

data class UpdateDriverLocation(val driverId: UUID, val longitude: Double, val latitude: Double,val vehicleType:String)