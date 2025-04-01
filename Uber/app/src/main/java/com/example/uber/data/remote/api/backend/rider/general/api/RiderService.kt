package com.example.uber.data.remote.api.backend.rider.general.api

import com.example.uber.data.remote.api.backend.rider.general.model.response.NearbyVehicleDetails
import com.example.uber.data.remote.api.backend.rider.general.model.response.NearbyVehiclesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.UUID

interface RiderService {
    @GET("api/ride/nearby/vehicle")
    suspend fun getNearbyVehicles(
        @Query("riderId") riderId : UUID,
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double
    ):Response<NearbyVehiclesResponse>

}