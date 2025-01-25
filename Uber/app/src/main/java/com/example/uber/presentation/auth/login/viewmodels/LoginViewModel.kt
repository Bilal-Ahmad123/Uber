package com.example.uber.presentation.auth.login.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.common.Resource
import com.example.uber.domain.use_case.auth.SignInUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val dispatcher: IDispatchers,
) : BaseViewModel(dispatcher) {
    private val _user = MutableLiveData<Resource<FirebaseUser>>()
    val user get() = _user
    fun signIn(task: Task<GoogleSignInAccount>) {
        launchOnBack {
            val result = signInUseCase(task)
            result.onSuccess {
                _user.postValue(Resource.Success(it!!))
            }.onFailure {
                _user.postValue(Resource.Error(it.message.toString()))
            }
        }
    }
}