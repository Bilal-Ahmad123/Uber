package com.example.uber.data.repository

import com.example.uber.data.remote.models.google.SuggetionsResponse.SuggestionsResponse
import com.example.uber.data.remote.models.google.directionsResponse.DirectionsResponse
import com.example.uber.data.remote.models.google.geoCodeResponse.GeoCodingGoogleMapsResponse
import com.example.uber.data.remote.models.google.placeDetails.PlaceDetails
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response

interface IGoogleRepository {
    suspend fun geoCodeLocation(latitude: Double, longitude: Double): Response<GeoCodingGoogleMapsResponse>
    suspend fun directionsResponse(origin:LatLng,destination:LatLng):Response<DirectionsResponse>
    suspend fun suggestionsResponse(input:String):Response<SuggestionsResponse>
    suspend fun getDetails(placeId:String):Response<PlaceDetails>
}