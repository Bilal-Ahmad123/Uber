package com.example.uber.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import com.example.uber.core.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.data.remote.models.mapbox.SuggestionResponse.SuggestionResponse
import com.example.uber.data.remote.models.mapbox.geoCodeResponse.GeoCodingResponse
import com.example.uber.domain.use_case.geocoding.MapboxUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MapboxViewModel @Inject constructor(
    private val mapboxUseCase: MapboxUseCase,
    private val dispatcher: IDispatchers,
) : BaseViewModel(dispatcher) {
    private val _locationName = MutableLiveData<Resource<GeoCodingResponse>>()
    private val _placesSuggestion = MutableLiveData<Resource<SuggestionResponse>>()
    val locationName get() = _locationName
    val placesSuggestion get() = _placesSuggestion

    fun geoCodeLocation(latitude: Double, longitude: Double) {
        launchOnBack {
            _locationName.postValue(Resource.Loading())
            val response = mapboxUseCase.getGeoCodeLocation(latitude, longitude)
            _locationName.postValue(handleResponse<GeoCodingResponse>(response))
        }
    }

    private fun <T>handleResponse(response: Response<T>): Resource<T>?{
        if(response.isSuccessful){
            return response.body()?.let {
                Resource.Success(it)
            }
        }

        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }

    fun getPlacesSuggestion(place:String){
        launchOnBack {
            _placesSuggestion.postValue(Resource.Loading())
            val response = mapboxUseCase.getSuggestions(place)
            _placesSuggestion.postValue(handleResponse<SuggestionResponse>(response))
        }
    }
}
