package com.example.uber.domain.use_case.getGeoCodeLocation

import com.example.uber.data.repository.IGetGeoCodeLocationRepository
import javax.inject.Inject

class GetGeoCodeLocationUseCase @Inject constructor(private val repository: IGetGeoCodeLocationRepository) {
     suspend fun getGeoCodeLocation(latitude:Double,longitude:Double) = repository.geoCodeLocation(latitude,longitude)
}