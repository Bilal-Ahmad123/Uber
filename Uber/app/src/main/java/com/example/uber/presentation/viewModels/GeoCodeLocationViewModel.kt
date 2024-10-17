package com.example.uber.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import com.example.uber.app.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.remote.GeoCode.MapBox.GeoCodingResponse
import com.example.uber.domain.use_case.getGeoCodeLocation.GetGeoCodeLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GeoCodeLocationViewModel @Inject constructor(
    private val getGeoCodeLocation: GetGeoCodeLocationUseCase,
    private val dispatcher: IDispatchers
) : BaseViewModel(dispatcher) {
    private val _locationName = MutableLiveData<Resource<GeoCodingResponse>>()
    val locationName get() = _locationName

    fun geoCodeLocation(latitude: Double, longitude: Double) {
        launchOnBack {
            _locationName.postValue(Resource.Loading())
            val response = getGeoCodeLocation.getGeoCodeLocation(latitude, longitude)
            _locationName.postValue(handleResponse(response))
        }
    }

    private fun handleResponse(response: Response<GeoCodingResponse>): Resource<GeoCodingResponse>?{
        if(response.isSuccessful){
            return response.body()?.let {
                Resource.Success(it)
            }
        }

        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }
}
