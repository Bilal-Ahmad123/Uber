package com.example.uber.presentation.riderpresentation.viewModels

import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.common.Resource
import com.example.uber.data.remote.api.backend.rider.general.model.response.NearbyVehiclesResponse
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.example.uber.domain.remote.general.usecase.GetNearbyVehicles
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(dispatcher : IDispatchers,
    private val getNearbyVehiclesUseCase : GetNearbyVehicles
) : BaseViewModel(dispatcher) {
    private val _nearbyVehicles = MutableStateFlow<Resource<List<NearbyVehicles>>?>(null)
    val nearbyVehicles get() = _nearbyVehicles
    private var _riderId:UUID? = null
    val riderId get() = _riderId

    fun getNearbyVehicles(riderId :UUID, pickupLocation:LatLng){
        launchOnBack {
            _nearbyVehicles.emit(Resource.Loading())
            val response = getNearbyVehiclesUseCase(riderId,pickupLocation.latitude,pickupLocation.longitude)
            _nearbyVehicles.emit(handleResponse(response))
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

    fun setRiderId(riderId : UUID){
        _riderId = riderId
    }
}