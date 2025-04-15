package com.example.uber.presentation.riderpresentation.viewModels

import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.remote.api.backend.rider.socket.location.mapper.UpdateDriverLocation
import com.example.uber.domain.remote.socket.location.usecase.ObserveDriverLocationUseCase
import com.example.uber.domain.remote.socket.location.usecase.StartObservingDriverLocationUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val startObservingDriversLocationUseCase: StartObservingDriverLocationUseCase,
    private val observeDriversLocationsUseCase: ObserveDriverLocationUseCase,
) : BaseViewModel(dispatcher) {
    private val _driverLocation = MutableSharedFlow<UpdateDriverLocation>()
    val driverLocation: SharedFlow<UpdateDriverLocation>
        get() = _driverLocation.asSharedFlow()
    private var _pickUpLocation: LatLng? = null
    val pickUpLocation get() = _pickUpLocation

    fun setPickUpLocation(pickUpLocation: LatLng) {
        _pickUpLocation = pickUpLocation
    }

    fun startObservingDriversLocation() {
        startObservingDriversLocationUseCase()
    }

    fun observeDriversLocations() {
        launchOnBack {
            observeDriversLocationsUseCase().collect {
                _driverLocation.emit(it)
            }
        }
    }
}