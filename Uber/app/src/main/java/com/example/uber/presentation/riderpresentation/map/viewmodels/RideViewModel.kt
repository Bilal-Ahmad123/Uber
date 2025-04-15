package com.example.uber.presentation.riderpresentation.map.viewmodels

import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.RideRequest
import com.example.uber.domain.remote.socket.ride.usecase.RequestRideUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RideViewModel @Inject constructor(
    private val rideRequestUseCase: RequestRideUseCase,
    dispatcher: IDispatchers
) : BaseViewModel(dispatcher) {
    fun requestRide(requestRide: RideRequest) {
        launchOnBack {
            rideRequestUseCase(requestRide)
        }
    }
}