package com.example.uber.data.remote.api.backend.authentication.api

import com.example.uber.data.remote.api.backend.authentication.models.requestModels.RiderDetailsRequest
import com.example.uber.data.remote.api.backend.authentication.models.responseModels.CheckRiderExistsResponse
import com.example.uber.data.remote.api.backend.authentication.models.responseModels.RiderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IAuthenticationService {
    @POST("api/create/rider")
    suspend fun registerRider(
        @Body Rider: RiderDetailsRequest
    ):Response<RiderResponse>

    @GET("api/rider/exists")
    suspend fun checkUserExists(
        @Query("email") email:String
    ):Response<CheckRiderExistsResponse>
}