package com.example.uber.presentation.riderpresentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uber.core.Dispatchers.IDispatchers
import com.example.uber.core.base.BaseViewModel
import com.google.android.gms.maps.model.LatLng
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

    fun cleanData(){
        pickUpAnnotationClicked.postValue(false)
        dropOffAnnotationClicked.postValue(false)
    }


}