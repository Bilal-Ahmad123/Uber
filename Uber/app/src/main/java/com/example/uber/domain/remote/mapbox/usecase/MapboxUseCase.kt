package com.example.uber.domain.remote.mapbox.usecase

import com.example.uber.data.remote.api.mapBox.repository.IMapBoxRepository
import javax.inject.Inject

class MapboxUseCase @Inject constructor(private val repository: IMapBoxRepository) {
     suspend fun getGeoCodeLocation(latitude:Double,longitude:Double) = repository.geoCodeLocation(latitude,longitude)
     suspend fun getSuggestions(place:String) = repository.getSuggestions(place)
     suspend fun retrieveSuggestedPlaceDetail(mapboxId:String) = repository.retrieveSuggestedPlaceDetail(mapboxId)
}