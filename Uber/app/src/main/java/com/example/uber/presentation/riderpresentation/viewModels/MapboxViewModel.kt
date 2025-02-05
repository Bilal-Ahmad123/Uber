package com.example.uber.presentation.riderpresentation.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.uber.core.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.utils.FetchLocation
import com.example.uber.data.local.entities.Location
import com.example.uber.data.remote.models.mapbox.RetrieveSuggestedPlaceDetail.RetrieveSuggestResponse
import com.example.uber.data.remote.models.mapbox.SuggestionResponse.SuggestionResponse
import com.example.uber.data.remote.models.mapbox.geoCodeResponse.GeoCodingResponse
import com.example.uber.domain.use_case.geocoding.LocationUseCase
import com.example.uber.domain.use_case.geocoding.MapboxUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MapboxViewModel @Inject constructor(
    private val mapboxUseCase: MapboxUseCase,
    private val dispatcher: IDispatchers,
    @ApplicationContext private val context: Context,
    private val locationUseCase: LocationUseCase
) : BaseViewModel(dispatcher) {
    private val _locationNameGeoCoded = MutableLiveData<Resource<GeoCodingResponse>>()
    private val _placesSuggestion = MutableLiveData<Resource<SuggestionResponse>>()
    private var _retrieveSuggestedPlaceDetail = MutableLiveData<List<Double>>()
    private val _locationName = MutableLiveData<String>()
    val placesSuggestion get() = _placesSuggestion
    val retrieveSuggestedPlaceDetail get() = _retrieveSuggestedPlaceDetail
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
            _locationNameGeoCoded.postValue(Resource.Loading())
            val response = mapboxUseCase.getGeoCodeLocation(latitude, longitude)
            _locationNameGeoCoded.postValue(handleResponse<GeoCodingResponse>(response))
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

    fun getPlacesSuggestion(place: String) {
        launchOnBack {
            _placesSuggestion.postValue(Resource.Loading())
            val response = mapboxUseCase.getSuggestions(place)
            _placesSuggestion.postValue(handleResponse<SuggestionResponse>(response))
        }
    }

    fun retrieveSuggestedPlaceDetail(mapboxId: String) {
        launchOnBack {
            var response = mapboxUseCase.retrieveSuggestedPlaceDetail(mapboxId)
            _retrieveSuggestedPlaceDetail.postValue(
                handleResponse<RetrieveSuggestResponse>(response)?.data?.features?.get(
                    0
                )?.geometry?.coordinates
            )

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

    fun saveCurrentLocationToDB(currentLocation: Location) {
        launchOnBack {
            locationUseCase.insertCurrentLocation(currentLocation)
        }
    }

    fun setLatitudeAndLongitudeIfNoNetworkOrGPS() {
        launchOnBack {
            val location = locationUseCase.getCurrentLocation()
            if (location != null) {
                _pickUpLatitude = location.location.latitude
                _pickUpLongitude = location.location.longitude
                _dropOffLatitude = location.location.latitude
                _dropOffLongitude = location.location.longitude
            }
        }
    }
}
