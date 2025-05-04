package com.example.uber.presentation.riderpresentation.viewModels

import android.util.Log
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.common.Resource
import com.example.uber.data.remote.api.backend.rider.socket.ride.model.TripLocation
import com.example.uber.data.remote.api.backend.rider.socket.trip.model.DriverReachedPickUpSpot
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uber.domain.remote.google.usecase.GoogleUseCase
import com.example.uber.domain.remote.socket.ride.usecase.ObserveRideAcceptedUseCase
import com.example.uber.domain.remote.socket.trip.usecase.DriverReachedPickUpSpotUseCase
import com.example.uber.domain.remote.socket.trip.usecase.ObserveTripLocationsUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val googleUseCase: GoogleUseCase,
    private val observeTripLocationsUseCase: ObserveTripLocationsUseCase,
    private val driverReachedPickUpSpotUseCase: DriverReachedPickUpSpotUseCase
    ) : BaseViewModel(dispatcher) {
    private val _directions = MutableSharedFlow<Resource<DirectionsResponse>?>()
    val directions get() = _directions.asSharedFlow()

    private val tripLocation = MutableSharedFlow<TripLocation>()
    val tripUpdates get() = tripLocation.asSharedFlow()


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
            _directions.emit(Resource.Loading())
            val response = googleUseCase.directionsRequest(origin, destination)
            _directions.emit(handleResponse<DirectionsResponse>(response))
        }
    }


    fun observeTripLocation(){
        launchOnBack {
            observeTripLocationsUseCase().collectLatest {
                tripLocation.emit(it)
            }
        }
    }

   suspend fun driverReachedPickUpSpot():Flow<DriverReachedPickUpSpot>{
       return driverReachedPickUpSpotUseCase()
   }
}