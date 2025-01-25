package com.example.uber.domain.repository

import android.content.Intent
import android.widget.Toast
import com.example.uber.data.repository.IAuthRepository
import com.example.uber.presentation.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl:IAuthRepository{
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
    override suspend fun signIn(task: Task<GoogleSignInAccount>):Result<FirebaseUser?> {
        var user: FirebaseUser? = null
        return try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
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