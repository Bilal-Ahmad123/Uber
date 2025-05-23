package com.example.uber.presentation.riderpresentation.map.viewmodels

import android.util.Log
import androidx.lifecycle.asLiveData
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideAccepted
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideRequest
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.TripLocation
import com.example.uber.domain.remote.socket.trip.usecase.ObserveTripLocationsUseCase
import com.example.uber.domain.remote.socket.ride.usecase.RequestRideUseCase
import com.example.uber.domain.remote.socket.ride.usecase.StartObservingRideAcceptedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class RideViewModel @Inject constructor(
    private val rideRequestUseCase: RequestRideUseCase,
    private val startObservingRideAcceptedUseCase: StartObservingRideAcceptedUseCase,
    dispatcher: IDispatchers
) : BaseViewModel(dispatcher) {
    private val rideAccepted = MutableStateFlow<RideAccepted?>(null)
    val rideAccept get() = rideAccepted.asStateFlow()

    fun requestRide(requestRide: RideRequest) {
        launchOnBack {
            rideRequestUseCase(requestRide)
        }
    }

    fun startObservingRideRequestAccepted() {
        launchOnBack {
           startObservingRideAcceptedUseCase().collectLatest {
               rideAccepted.emit(it)
           }
        }
    }



}