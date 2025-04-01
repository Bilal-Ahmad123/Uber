package com.example.uber.domain.remote.general.usecase

import com.example.uber.data.remote.api.backend.rider.general.model.response.NearbyVehiclesResponse
import com.example.uber.data.remote.api.backend.rider.general.repository.RiderRepository
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class GetNearbyVehicles @Inject constructor(private val riderRepository: RiderRepository){
    suspend operator fun invoke(riderId : UUID, latitude : Double, longitude : Double):Response<List<NearbyVehicles>>{
        return riderRepository.getNearbyVehicles(riderId,latitude,longitude)
    }
}