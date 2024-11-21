package com.example.uber.presentation.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.uber.core.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.utils.FetchLocation
import com.example.uber.data.remote.models.google.directionsResponse.DirectionsResponse
import com.example.uber.data.remote.models.google.geoCodeResponse.GeoCodingGoogleMapsResponse
import com.example.uber.domain.use_case.geocoding.GoogleUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GoogleViewModel @Inject constructor(
    private val googleUseCase: GoogleUseCase,
    private val dispatcher: IDispatchers,
    @ApplicationContext private val context: Context,
) : BaseViewModel(dispatcher) {

    private val _locationName = MutableLiveData<Resource<GeoCodingGoogleMapsResponse>>()
    val locationName get() = _locationName
    private val _directions = MutableLiveData<Resource<DirectionsResponse>>()
    val directions get() = _directions
    private var _pickUpLatitude: Double = 0.0
    private var _pickUpLongitude: Double = 0.0
    val pickUpLatitude get() = _pickUpLatitude
    val pickUpLongitude get() = _pickUpLongitude
    private var _dropOffLatitude: Double = 0.0
    private var _dropOffLongitude: Double = 0.0
    val dropOffLatitude get() = _dropOffLatitude
    val dropOffLongitude get() = _dropOffLongitude
    private var _pickUpLocationName = MutableLiveData<String>()
    val pickUpLocationName get() = _pickUpLocationName
    private var _dropOffLocationName = MutableLiveData<String>()
    val dropOffLocationName get() = _dropOffLocationName

    fun geoCodeLocation(latitude: Double, longitude: Double) {
        launchOnBack {
            _locationName.postValue(Resource.Loading())
            val response = googleUseCase.getGeoCodeLocation(latitude, longitude)
            _locationName.postValue(handleResponse(response))
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

    fun directionsRequest(origin: LatLng, destination: LatLng) {
        launchOnBack {
            val response = googleUseCase.directionsRequest(origin, destination)
            _directions.postValue(handleResponse<DirectionsResponse>(response))
        }
    }

    fun setPickUpLocationName(latitude: Double, longitude: Double) {
        launchOnBack {
            val res = FetchLocation.getLocation(latitude, longitude, context)
            _pickUpLocationName.postValue(res)
        }
        this._pickUpLatitude = latitude
        this._pickUpLongitude = longitude
    }

    fun setDropOffLocationName(latitude: Double, longitude: Double) {
        launchOnBack {
            val res = FetchLocation.getLocation(latitude, longitude, context)
            _dropOffLocationName.postValue(res)
        }
        this._dropOffLatitude = latitude
        this._dropOffLongitude = longitude

    }
}