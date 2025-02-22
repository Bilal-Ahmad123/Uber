package com.example.uber.presentation.splash.viewmodel

import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.common.Resource
import com.example.uber.domain.local.rider.model.Rider
import com.example.uber.domain.local.rider.usecase.GetRider
import com.example.uber.domain.local.rider.usecase.InsertRider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RiderRoomViewModel @Inject constructor(
    private val getRiderUseCase: GetRider,
    private val insertDriverUseCase: InsertRider,
    dispatcher: IDispatchers
) : BaseViewModel(dispatcher) {
    private val _rider = MutableStateFlow<Resource<Rider?>>(Resource.Loading())
    val rider get() = _rider
    fun getRider() {
        launchOnDb {
            _rider.emit(Resource.Loading())
            val response = getRiderUseCase()
            _rider.emit(Resource.Success(response))
        }
    }

    fun insertRider(rider: Rider) {
        launchOnDb {
            insertDriverUseCase(rider)
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