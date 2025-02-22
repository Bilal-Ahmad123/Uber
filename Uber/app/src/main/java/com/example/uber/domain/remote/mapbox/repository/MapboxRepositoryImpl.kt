package com.example.uber.domain.remote.mapbox.repository


import com.example.uber.data.remote.api.mapBox.api.MapboxService
import com.example.uber.data.remote.api.mapBox.models.RetrieveSuggestedPlaceDetail.RetrieveSuggestResponse
import com.example.uber.data.remote.api.mapBox.models.SuggestionResponse.SuggestionResponse
import com.example.uber.data.remote.api.mapBox.models.geoCodeResponse.GeoCodingResponse
import com.example.uber.data.remote.api.mapBox.repository.IMapBoxRepository
import retrofit2.Response
import javax.inject.Inject

class MapboxRepositoryImpl @Inject constructor(private val mapBoxApi: MapboxService) :
    IMapBoxRepository {
    override suspend fun geoCodeLocation(latitude:Double,longitude:Double): Response<GeoCodingResponse> {
        return mapBoxApi.geoCodeLocation(latitude,longitude)
    }

    override suspend fun getSuggestions(place: String): Response<SuggestionResponse> {
        return mapBoxApi.getSuggestions(place)
    }

    override suspend fun retrieveSuggestedPlaceDetail(mapboxId: String): Response<RetrieveSuggestResponse> {
        return mapBoxApi.retrieveSuggestedPlaceDetail(mapboxId)
    }

}