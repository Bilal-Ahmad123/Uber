package com.example.uber.data.repository

import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
     suspend fun signIn(task: SignInCredential):Result<FirebaseUser?>
}