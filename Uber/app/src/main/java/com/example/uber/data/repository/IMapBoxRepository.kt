package com.example.uber.data.repository

import com.example.uber.data.remote.models.mapbox.SuggestionResponse.SuggestionResponse
import com.example.uber.data.remote.models.mapbox.geoCodeResponse.GeoCodingResponse
import retrofit2.Response

interface IMapBoxRepository {
    suspend fun geoCodeLocation(latitude:Double,longitude:Double): Response<GeoCodingResponse>
    suspend fun getSuggestions(place:String): Response<SuggestionResponse>
}