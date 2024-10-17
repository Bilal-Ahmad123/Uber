package com.example.uber.domain.use_case.locations

import com.example.uber.data.repository.IPickUpLocationRepository
import com.example.uber.domain.model.PickUpLocation
import javax.inject.Inject

class PickUpLocationUseCase @Inject constructor(private val repository: IPickUpLocationRepository) {
    suspend fun getPickUpLocation() = repository.getPickUpLocation()
    suspend fun insertPickUpLocation(pickUpLocation: PickUpLocation) = repository.insertPickUpLocation(pickUpLocation)
    suspend fun deletePickUpLocation() = repository.deletePickUpLocation()
    suspend fun getGeoCodeLocation(latitude:Double,longitude:Double) = repository.getLocationFromGoogleApi(latitude,longitude)

}