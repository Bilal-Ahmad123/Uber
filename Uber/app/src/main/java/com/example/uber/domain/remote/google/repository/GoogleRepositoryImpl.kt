package com.example.uber.domain.remote.google.repository

import com.example.uber.data.remote.api.googleMaps.models.geoCodeResponse.GeoCodingGoogleMapsResponse
import com.example.uber.data.remote.api.googleMaps.api.GoogleMapService
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uber.data.remote.api.googleMaps.repository.IGoogleRepository
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import javax.inject.Inject

 class GoogleRepositoryImpl @Inject constructor(private val googleApi: GoogleMapService) :
     IGoogleRepository {
    override suspend fun geoCodeLocation(
        latitude: Double,
        longitude: Double
    ): Response<GeoCodingGoogleMapsResponse> {
        return googleApi.geoCodeLocation("${latitude},${longitude}")
    }

    override suspend fun directionsResponse(
        origin: LatLng,
        destination: LatLng
    ): Response<DirectionsResponse> {
        return googleApi.directionsRequest(
            "${origin.latitude},${origin.longitude}",
            "${destination.latitude},${destination.longitude}"
        )
    }

    override suspend fun suggestionsResponse(input: String) = googleApi.suggestionsResponse(input)

    override suspend fun getDetails(placeId: String) = googleApi.placeDetails(placeId)
}