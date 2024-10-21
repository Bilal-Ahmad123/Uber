package com.example.uber.presentation.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.uber.app.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
//import com.example.uber.core.utils.FetchLocation
import com.example.uber.data.remote.GeoCode.GoogleMaps.GeoCodingGoogleMapsResponse
import com.example.uber.domain.model.DropOffLocation
import com.example.uber.domain.model.PickUpLocation
import com.example.uber.domain.use_case.locations.DropOffLocationUseCase
import com.example.uber.domain.use_case.locations.PickUpLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DropOffLocationViewModel @Inject constructor(
    private val dropOffLocationUseCase: DropOffLocationUseCase,
    private val dispatcher: IDispatchers,
    @ApplicationContext private val context: Context
) : BaseViewModel(dispatcher) {
    private val _locationName = MutableLiveData<String>()
    val locationName get() = _locationName
    private var _latitude: Double = 0.0
    private var _longitude: Double = 0.0
    val latitude get() = _latitude
    val longitude get() = _longitude

    fun geoCodeLocation(latitude: Double, longitude: Double) {
        launchOnBack {
//            _locationName.postValue(Resource.Loading())
//            val response = dropOffLocationUseCase.getGeoCodeLocation(latitude, longitude)
//            _locationName.postValue(handleResponse(response))
        }
    }

    private fun handleResponse(response: Response<GeoCodingGoogleMapsResponse>): Resource<GeoCodingGoogleMapsResponse>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }

        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }

    fun setPickUpLocationName(latitude: Double, longitude: Double) {
        this._latitude = latitude
        this._longitude = longitude
        launchOnBack {
//            val res = FetchLocation.getLocation(latitude, longitude, context)
//            _locationName.postValue(res)
        }


    }
}