package com.example.uber.presentation.riderpresentation.viewModels

import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(dispatcher:IDispatchers) : BaseViewModel(dispatcher){
    private var _pickUpLocation : LatLng? = null
    val pickUpLocation get() = _pickUpLocation

    fun setPickUpLocation(pickUpLocation: LatLng){
        _pickUpLocation = pickUpLocation
    }
}