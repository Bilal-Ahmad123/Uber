package com.example.uber.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import com.example.uber.core.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.remote.models.google.geoCodeResponse.GeoCodingGoogleMapsResponse
import com.example.uber.domain.use_case.geocoding.GoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GoogleViewModel @Inject constructor(
    private val getGeoCodeLocation: GoogleUseCase,
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