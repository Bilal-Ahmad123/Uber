package com.example.uber.domain.use_case.geocoding

import com.example.uber.data.repository.IGoogleRepository
import javax.inject.Inject

class GoogleUseCase @Inject constructor(private val repository: IGoogleRepository) {
    suspend fun getGeoCodeLocation(latitude:Double,longitude:Double) = repository.geoCodeLocation(latitude,longitude)
}