package com.example.uber.domain.use_case.locations

import com.example.uber.data.repository.IDropOffLocationRepository
import com.example.uber.domain.model.DropOffLocation
import javax.inject.Inject

class DropOffLocationUseCase @Inject constructor(private val repository: IDropOffLocationRepository) {
    suspend fun getDropOffLocation() = repository.getDropOffLocation()
    suspend fun insertDropOffLocation(dropOffLocation: DropOffLocation) =
        repository.insertDropOffLocation(dropOffLocation)

    suspend fun deleteDropOffLocation() = repository.deleteDropOffLocation()
    suspend fun getGeoCodeLocation(latitude:Double,longitude:Double) = repository.getLocationFromGoogleApi(latitude,longitude)

}