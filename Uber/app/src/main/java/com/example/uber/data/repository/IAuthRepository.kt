package com.example.uber.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
     suspend fun signIn(task: Task<GoogleSignInAccount>):Result<FirebaseUser?>
}