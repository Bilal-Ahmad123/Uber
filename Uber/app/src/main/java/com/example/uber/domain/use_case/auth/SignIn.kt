package com.example.uber.domain.use_case.auth

import com.example.uber.data.repository.IAuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val authRepository: IAuthRepository) {
    suspend operator fun invoke(task: Task<GoogleSignInAccount>):Result<FirebaseUser?>{
        return authRepository.signIn(task)
    }
}