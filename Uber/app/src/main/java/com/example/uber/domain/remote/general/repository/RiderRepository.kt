package com.example.uber.domain.remote.general.repository

import com.example.uber.data.remote.api.backend.rider.general.api.RiderService
import com.example.uber.data.remote.api.backend.rider.general.mapper.toDomain
import com.example.uber.data.remote.api.backend.rider.general.model.response.NearbyVehiclesResponse
import com.example.uber.data.remote.api.backend.rider.general.repository.RiderRepository
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.lang.Exception
import java.util.UUID

class RiderRepository (private val riderService : RiderService) : RiderRepository {
    override suspend fun getNearbyVehicles(
        riderId: UUID,
        latitude: Double,
        longitude: Double
    ): Response<List<NearbyVehicles>> {
        return try {
            Response.success(riderService.getNearbyVehicles(riderId, latitude, longitude).body()?.nearbyVehicles?.map { it.toDomain() })
        } catch (e:Exception){
            Response.error(500, "Network error: ${e.localizedMessage}".toResponseBody(null))
        }
    }
}