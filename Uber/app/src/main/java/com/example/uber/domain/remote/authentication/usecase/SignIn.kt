package com.example.uber.domain.remote.authentication.usecase

import com.example.uber.data.remote.api.backend.authentication.repository.IAuthRepository
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val authRepository: IAuthRepository) {
    suspend operator fun invoke(task: SignInCredential):Result<FirebaseUser?>{
        return authRepository.signIn(task)
    }
}