package com.example.uber.presentation.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.uber.app.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.utils.FetchLocation
import com.example.uber.data.remote.GeoCode.MapBox.GeoCodingResponse
import com.example.uber.domain.use_case.getGeoCodeLocation.GetGeoCodeLocationUseCase
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GeoCodeLocationViewModel @Inject constructor(
    private val getGeoCodeLocation: GetGeoCodeLocationUseCase,
    private val dispatcher: IDispatchers,
    @ApplicationContext private val context: Context
) : BaseViewModel(dispatcher) {
    private val _location = MutableLiveData<Point>()
    val location get() = _location

    fun geoCodeLocation() {
        launchOnBack {
//            _location.postValue(FetchLocation.getCurrentLocation(context))
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
