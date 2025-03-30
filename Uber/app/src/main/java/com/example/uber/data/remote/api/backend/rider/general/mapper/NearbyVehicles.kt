package com.example.uber.data.remote.api.backend.rider.general.mapper

import com.example.uber.data.remote.api.backend.rider.general.model.response.NearbyVehicleDetails
import com.example.uber.domain.remote.general.model.response.NearbyVehicles

fun NearbyVehicleDetails.toDomain():NearbyVehicles{
    return NearbyVehicles(vehicleName,maxSeats,fare,imageUrl)
}