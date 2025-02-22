package com.example.uber.data.remote.api.backend.rider.location.mapper

import com.example.uber.domain.remote.location.model.UpdateLocation
import java.util.UUID

fun UpdateLocation.toData():UpdateRiderLocation{
    return UpdateRiderLocation(riderId, longitude, latitude)
}

data class UpdateRiderLocation(val userId:UUID, val longitude:Double, val latitude:Double)