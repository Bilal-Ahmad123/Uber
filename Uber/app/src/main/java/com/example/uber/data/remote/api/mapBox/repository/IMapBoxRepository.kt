package com.example.uber.data.remote.api.mapBox.repository

import com.example.uber.data.remote.api.mapBox.models.RetrieveSuggestedPlaceDetail.RetrieveSuggestResponse
import com.example.uber.data.remote.api.mapBox.models.SuggestionResponse.SuggestionResponse
import com.example.uber.data.remote.api.mapBox.models.geoCodeResponse.GeoCodingResponse
import retrofit2.Response

interface IMapBoxRepository {
    suspend fun geoCodeLocation(latitude:Double,longitude:Double): Response<GeoCodingResponse>
    suspend fun getSuggestions(place:String): Response<SuggestionResponse>
    suspend fun retrieveSuggestedPlaceDetail(mapboxId:String): Response<RetrieveSuggestResponse>
}