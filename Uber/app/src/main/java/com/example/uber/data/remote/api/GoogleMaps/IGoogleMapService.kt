package com.example.uber.data.remote.api.GoogleMaps
import com.example.uber.BuildConfig
import com.example.uber.data.remote.models.google.directionsResponse.DirectionsResponse
import com.example.uber.data.remote.models.google.geoCodeResponse.GeoCodingGoogleMapsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IGoogleMapService {
    @GET("maps/api/geocode/json")
    suspend fun geoCodeLocation(
        @Query("latlng") latitude:String,
        @Query("key") accessToken: String =  BuildConfig.GOOGLE_API_KEY
    ): Response<GeoCodingGoogleMapsResponse>

    @GET("maps/api/directions/json")
    suspend fun directionsRequest(
        @Query("origin") origin:String,
        @Query("destination") destination:String,
        @Query("key") accessToken: String =  BuildConfig.GOOGLE_API_KEY,
        @Query("mode") mode:String = "driving"
    ):Response<DirectionsResponse>
}