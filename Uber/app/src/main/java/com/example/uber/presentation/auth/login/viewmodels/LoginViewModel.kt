package com.example.uber.presentation.auth.login.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.common.Resource
import com.example.uber.data.remote.api.backend.authentication.models.responseModels.CheckRiderExistsResponse
import com.example.uber.domain.remote.authentication.model.response.CheckRiderExists
import com.example.uber.domain.use_case.auth.CheckIfUserExistsUseCase
import com.example.uber.domain.use_case.auth.SignInUseCase
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val dispatcher: IDispatchers,
    private val checkIfUserExistsUseCase: CheckIfUserExistsUseCase
) : BaseViewModel(dispatcher) {
    private val _user = MutableLiveData<Resource<FirebaseUser>>()
    val user get() = _user
    private val _userExists = MutableLiveData<Resource<CheckRiderExists>>()
    val userExists get() = _userExists
    fun signIn(task: SignInCredential) {
        launchOnBack {
            val result = signInUseCase(task)
            result.onSuccess {
                _user.postValue(Resource.Success(it!!))
            }.onFailure {
                _user.postValue(Resource.Error(it.message.toString()))
            }
        }
    }

    fun checkIfUserExists(email:String) {
        launchOnBack {
            _userExists.postValue(Resource.Loading())
            val result = checkIfUserExistsUseCase(email)
            _userExists.postValue(handleResponse(result))
        }
    }

    private fun <T> handleResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }

        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }
}