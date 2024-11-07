package com.example.uber.domain.use_case.geocoding

import com.example.uber.data.repository.IMapBoxRepository
import javax.inject.Inject

class MapboxUseCase @Inject constructor(private val repository: IMapBoxRepository) {
     suspend fun getGeoCodeLocation(latitude:Double,longitude:Double) = repository.geoCodeLocation(latitude,longitude)
     suspend fun getSuggestions(place:String) = repository.getSuggestions(place)
}