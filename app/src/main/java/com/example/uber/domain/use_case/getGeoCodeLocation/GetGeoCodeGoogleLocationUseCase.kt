package com.example.uber.domain.use_case.getGeoCodeLocation

import com.example.uber.data.repository.IGetGeoCodeGoogleLocationRepository
import javax.inject.Inject

class GetGeoCodeGoogleLocationUseCase @Inject constructor(private val repository: IGetGeoCodeGoogleLocationRepository) {
    suspend fun getGeoCodeLocation(latitude:Double,longitude:Double) = repository.geoCodeLocation(latitude,longitude)
}