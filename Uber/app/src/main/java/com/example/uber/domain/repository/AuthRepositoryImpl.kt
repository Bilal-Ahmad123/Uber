package com.example.uber.domain.repository

import com.example.uber.data.remote.api.backend.authentication.api.IAuthenticationService
import com.example.uber.data.remote.api.backend.authentication.mapper.toDomain
import com.example.uber.data.remote.api.backend.authentication.models.requestModels.RiderDetailsRequest
import com.example.uber.data.remote.api.backend.authentication.models.requestModels.RiderRequest
import com.example.uber.data.remote.api.backend.authentication.models.responseModels.RiderResponse
import com.example.uber.data.remote.api.backend.authentication.repository.IAuthRepository
import com.example.uber.domain.remote.authentication.model.response.CheckRiderExists
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class AuthRepositoryImpl(private val api: IAuthenticationService): IAuthRepository {
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
    override suspend fun signIn(task: SignInCredential):Result<FirebaseUser?> {
        var user: FirebaseUser? = null
        return try {
            val credential = GoogleAuthProvider.getCredential(task.googleIdToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user = auth.currentUser!!
                    }
                }.await()
            Result.success(user)
        } catch (exp: Exception) {
            Result.failure(exp)
        }
    }

    override suspend fun checkIfUserExists(email:String): Response<CheckRiderExists> {
        return try {
           Response.success(api.checkUserExists(email).body()?.toDomain())
        }catch (e:Exception){
            Response.error(500, "Network error: ${e.localizedMessage}".toResponseBody(null))
        }

    }

    override suspend fun registerRider(rider: RiderRequest):Response<RiderResponse> {
        return try{
            api.registerRider(RiderDetailsRequest(rider))
        }
        catch (e:Exception){
            Response.error(500, "Network error: ${e.localizedMessage}".toResponseBody(null))
        }
    }
}