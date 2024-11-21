package com.example.uber.domain.repository

import android.util.Log
import com.example.uber.data.remote.models.google.geoCodeResponse.GeoCodingGoogleMapsResponse
import com.example.uber.data.remote.api.GoogleMaps.IGoogleMapService
import com.example.uber.data.remote.models.google.directionsResponse.DirectionsResponse
import com.example.uber.data.repository.IGoogleRepository
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import javax.inject.Inject

class GoogleRepositoryImpl @Inject constructor(private val googleApi: IGoogleMapService) :
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
            "${origin.longitude},${origin.latitude}",
            "${destination.longitude},${destination.latitude}"
        )
    }
}