package com.example.uber.domain.repository

import com.example.uber.data.repository.IAuthRepository
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl:IAuthRepository{
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
}