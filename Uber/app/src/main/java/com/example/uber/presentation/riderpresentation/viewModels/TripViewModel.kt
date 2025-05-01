package com.example.uber.presentation.riderpresentation.viewModels

import android.util.Log
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.common.Resource
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uber.domain.remote.google.usecase.GoogleUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val googleUseCase: GoogleUseCase,
    ) : BaseViewModel(dispatcher) {
    private val _directions = MutableSharedFlow<Resource<DirectionsResponse>?>()
    val directions get() = _directions.asSharedFlow()

    private fun <T> handleResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }

        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }

    fun directionsRequest(origin: LatLng, destination: LatLng) {
        launchOnBack {
            val response = googleUseCase.directionsRequest(origin, destination)
            Log.d("directionsRequest", "directionsRequest: $response")
            _directions.emit(handleResponse<DirectionsResponse>(response))
        }
    }
}