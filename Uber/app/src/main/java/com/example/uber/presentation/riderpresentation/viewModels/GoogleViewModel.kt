package com.example.uber.presentation.riderpresentation.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.uber.core.common.Resource
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.utils.FetchLocation
import com.example.uber.data.local.location.entities.Location
import com.example.uber.data.remote.api.googleMaps.models.SuggetionsResponse.SuggestionsResponse
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uber.data.remote.api.googleMaps.models.geoCodeResponse.GeoCodingGoogleMapsResponse
import com.example.uber.data.remote.api.googleMaps.models.placeDetails.PlaceDetails
import com.example.uber.domain.remote.google.usecase.GoogleUseCase
import com.example.uber.domain.local.location.usecase.LocationUseCase
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
    private val locationUseCase: LocationUseCase
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
    private val _placesSuggestion = MutableLiveData<Resource<SuggestionsResponse>>()
    val placesSuggestion get() = _placesSuggestion
    private var _retrieveSuggestedPlaceDetail = MutableLiveData<List<Double>?>()
    val retrieveSuggestedPlaceDetail get() = _retrieveSuggestedPlaceDetail


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
            Log.d("directionsRequest", "directionsRequest: $response")
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

    fun getPlacesSuggestion(place: String) {
        launchOnBack {
            _placesSuggestion.postValue(Resource.Loading())
            val response = googleUseCase.suggestionsResponse(place)
            _placesSuggestion.postValue(handleResponse<SuggestionsResponse>(response))
        }
    }

    fun retrieveSuggestedPlaceDetail(placeId: String) {
        launchOnBack {
            var response = googleUseCase.getDetails(placeId)
            val handledResponse = handleResponse<PlaceDetails>(response)
            _retrieveSuggestedPlaceDetail.postValue(
                listOf(
                    handledResponse?.data?.result?.geometry?.location!!.lat,
                    handledResponse?.data?.result?.geometry?.location!!.lng
                )
            )

        }
    }

    fun cleanData(){
        _dropOffLatitude = 0.0
        _pickUpLatitude = 0.0
        _pickUpLongitude = 0.0
        _dropOffLongitude = 0.0
    }

    fun cleanRetreiveSuggestedData(){
        _retrieveSuggestedPlaceDetail.value = null
    }
}