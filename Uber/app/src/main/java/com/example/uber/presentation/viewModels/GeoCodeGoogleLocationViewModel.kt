package com.example.uber.presentation.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.uber.app.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.remote.GeoCode.GoogleMaps.GeoCodingGoogleMapsResponse
import com.example.uber.domain.use_case.getGeoCodeLocation.GetGeoCodeGoogleLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GeoCodeGoogleLocationViewModel @Inject constructor(
    private val getGeoCodeLocation: GetGeoCodeGoogleLocationUseCase,
    private val dispatcher: IDispatchers
) : BaseViewModel(dispatcher) {

    private val _locationName = MutableLiveData<Resource<GeoCodingGoogleMapsResponse>>()
    val locationName get() = _locationName

    fun geoCodeLocation(latitude: Double, longitude: Double) {
        launchOnBack {
            _locationName.postValue(Resource.Loading())
            val response = getGeoCodeLocation.getGeoCodeLocation(latitude, longitude)
            _locationName.postValue(handleResponse(response))
        }
    }

    private fun handleResponse(response: Response<GeoCodingGoogleMapsResponse>): Resource<GeoCodingGoogleMapsResponse>?{
        if(response.isSuccessful){
            return response.body()?.let {
                Resource.Success(it)
            }
        }

        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }
}