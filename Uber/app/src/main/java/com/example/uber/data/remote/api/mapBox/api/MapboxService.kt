package com.example.uber.data.remote.api.mapBox.api
import com.example.uber.BuildConfig
import com.example.uber.data.remote.api.mapBox.models.RetrieveSuggestedPlaceDetail.RetrieveSuggestResponse
import com.example.uber.data.remote.api.mapBox.models.SuggestionResponse.SuggestionResponse
import com.example.uber.data.remote.api.mapBox.models.geoCodeResponse.GeoCodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapboxService {
    @GET("search/geocode/v6/reverse")
    suspend fun geoCodeLocation(
        @Query("latitude") latitude:Double,
        @Query("longitude") longitude:Double,
        @Query("access_token") accessToken: String =  BuildConfig.MAPBOX_TOKEN
    ): Response<GeoCodingResponse>

    @GET("search/searchbox/v1/suggest")
    suspend fun getSuggestions(
        @Query("q") place:String,
        @Query("session_token") sessionToken:String = BuildConfig.SESSION_TOKEN,
        @Query("access_token") accessToken: String =  BuildConfig.MAPBOX_TOKEN,
        @Query("limit") limit:Int = 10
    ):Response<SuggestionResponse>

    @GET("search/searchbox/v1/retrieve/{mapbox_id}")
    suspend fun retrieveSuggestedPlaceDetail(
        @Path("mapbox_id") mapboxId:String,
        @Query("session_token") sessionToken:String = BuildConfig.SESSION_TOKEN,
        @Query("access_token") accessToken: String =  BuildConfig.MAPBOX_TOKEN,
    ):Response<RetrieveSuggestResponse>
}