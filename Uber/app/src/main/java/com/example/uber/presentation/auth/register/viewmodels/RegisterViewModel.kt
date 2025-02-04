package com.example.uber.presentation.auth.register.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.common.Resource
import com.example.uber.data.remote.api.backend.authentication.models.RequestModels.RiderRequest
import com.example.uber.data.remote.api.backend.authentication.models.ResponseModels.RiderResponse
import com.example.uber.domain.use_case.auth.CreateRiderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: CreateRiderUseCase,
    private val dispatcher: IDispatchers
) : BaseViewModel(dispatcher) {
    private val _rider = MutableLiveData<Resource<RiderResponse>>()
    val rider get() = _rider

    fun createRider(rider: RiderRequest) {
        launchOnBack {
            _rider.postValue(Resource.Loading())
            val result = registerUseCase(rider)
            _rider.postValue(handleResponse(result))
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