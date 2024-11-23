package com.example.uber.domain.use_case.geocoding

import com.example.uber.data.repository.IGoogleRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class GoogleUseCase @Inject constructor(private val repository: IGoogleRepository) {
    suspend fun getGeoCodeLocation(latitude:Double,longitude:Double) = repository.geoCodeLocation(latitude,longitude)
    suspend fun directionsRequest(origin:LatLng,destination:LatLng) = repository.directionsResponse(origin,destination)
    suspend fun suggestionsResponse(input:String) = repository.suggestionsResponse(input)
    suspend fun getDetails(placeId:String) = repository.getDetails(placeId)

}