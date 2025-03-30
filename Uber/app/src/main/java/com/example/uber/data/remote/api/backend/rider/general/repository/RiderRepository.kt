package com.example.uber.data.remote.api.backend.rider.general.repository

import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import retrofit2.Response
import java.util.UUID

interface RiderRepository {
    suspend fun getNearbyVehicles(riderId : UUID, latitude : Double, longitude : Double) : Response<List<NearbyVehicles>>
}