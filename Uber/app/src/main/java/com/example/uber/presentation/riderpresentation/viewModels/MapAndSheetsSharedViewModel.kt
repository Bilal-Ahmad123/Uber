package com.example.uber.presentation.riderpresentation.viewModels

import androidx.lifecycle.MutableLiveData
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.example.uber.core.enums.SheetState
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapAndSheetsSharedViewModel @Inject constructor(dispatcher: IDispatchers ) : BaseViewModel(dispatcher) {
    private val pickUpLatLng = MutableLiveData<LatLng>(null)
    val pickUpPosition get() = pickUpLatLng

    private val dropOffLatLng = MutableLiveData<LatLng>(null)
    val dropOffPosition get() = dropOffLatLng

    private val pickUpAnnotationClicked  = MutableLiveData<Boolean>(false)
    val pickUpAnnotationClick get() = pickUpAnnotationClicked

    private val dropOffAnnotationClicked  = MutableLiveData<Boolean>(false)
    val dropOffAnnotationClick get() = dropOffAnnotationClicked

    private val isDestinationConfirmBtnClicked = MutableLiveData<Boolean>(false)
    val isBtnDestinationClicked get() = isDestinationConfirmBtnClicked

    private val isPickInputInFocus = MutableLiveData<Boolean>(false)
    val pickUpInputInFocus get() = isPickInputInFocus

    private val isDropOffInputInFocus = MutableLiveData<Boolean>(true)
    val dropOffInputInFocus get() = isDropOffInputInFocus

    private val currentOpenedSheet = MutableLiveData<SheetState>(SheetState.PICKUP_SHEET)
    val currentSheet get() = currentOpenedSheet

    private val rideOptionsSheetCurrentOffset = MutableLiveData<Int>()
    val sheetOffset get() = rideOptionsSheetCurrentOffset

    private var selectedVehicle : NearbyVehicles ? = null
    val vehicleSelected get() = selectedVehicle

    private var vehicleGotSelected  = MutableLiveData<NearbyVehicles>()
    val vehicleSelect get() = vehicleGotSelected

    private var _bounds : LatLngBounds ? = null
    val bounds get() = _bounds

    fun setPickUpLatLng(pickUpLatLng:LatLng){
        this.pickUpLatLng.postValue(pickUpLatLng)
    }

    fun setDropOffLatLng(pickUpLatLng:LatLng){
        this.dropOffLatLng.postValue(pickUpLatLng)
    }

    fun setIsDestinationConfirmBtnClicked(value : Boolean){
        isDestinationConfirmBtnClicked.postValue(value)
    }

    fun setPickUpAnnotationClicked(value : Boolean){
        pickUpAnnotationClicked.postValue(value)
    }

    fun setDropOffAnnotationClicked(value : Boolean){
        dropOffAnnotationClicked.postValue(value)
    }

    fun setPickUpInputInFocus(value : Boolean){
        isPickInputInFocus.postValue(value)
        isDropOffInputInFocus.postValue(!value)
    }

    fun setDropOffInputInFocus(value : Boolean){
        isDropOffInputInFocus.postValue(value)
        isPickInputInFocus.postValue(!value)
    }

    fun setCurrentOpenedSheet(value : SheetState){
        currentOpenedSheet.postValue(value)
    }

    fun setRideOptionsSheetOffsetAndBounds(value: Int, bounds: LatLngBounds){
        rideOptionsSheetCurrentOffset.postValue(value)
        _bounds = bounds
    }

    fun setSelectedVehicle(value : NearbyVehicles){
        selectedVehicle = value
    }

    fun vehicleSelected(value : NearbyVehicles){
        vehicleGotSelected.postValue(value)
    }



    fun cleanData(){
        isDropOffInputInFocus.postValue(false)
        isPickInputInFocus.postValue(false)
    }


}