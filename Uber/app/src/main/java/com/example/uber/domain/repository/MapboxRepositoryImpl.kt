package com.example.uber.domain.repository


import com.example.uber.data.remote.api.MapBox.IMapboxService
import com.example.uber.data.remote.models.mapbox.SuggestionResponse.SuggestionResponse
import com.example.uber.data.remote.models.mapbox.geoCodeResponse.GeoCodingResponse
import com.example.uber.data.repository.IMapBoxRepository
import retrofit2.Response
import javax.inject.Inject

class MapboxRepositoryImpl @Inject constructor(private val mapBoxApi: IMapboxService) : IMapBoxRepository {
    override suspend fun geoCodeLocation(latitude:Double,longitude:Double): Response<GeoCodingResponse> {
        return mapBoxApi.geoCodeLocation(latitude,longitude)
    }

    override suspend fun getSuggestions(place: String): Response<SuggestionResponse> {
        return mapBoxApi.getSuggestions(place)
    }

}