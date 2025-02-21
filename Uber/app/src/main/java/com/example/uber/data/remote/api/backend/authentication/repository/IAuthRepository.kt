package com.example.uber.data.remote.api.backend.authentication.repository

import com.example.uber.data.remote.api.backend.authentication.models.requestModels.RiderRequest
import com.example.uber.data.remote.api.backend.authentication.models.responseModels.RiderResponse
import com.example.uber.domain.remote.authentication.model.response.CheckRiderExists
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser
import retrofit2.Response

interface IAuthRepository {
     suspend fun signIn(task: SignInCredential):Result<FirebaseUser?>
     suspend fun checkIfUserExists(email:String): retrofit2.Response<CheckRiderExists>
     suspend fun registerRider(rider: RiderRequest): Response<RiderResponse>
}