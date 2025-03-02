package com.example.uber.core.interfaces

import com.google.android.gms.maps.model.LatLng


interface IActions {
    fun onBottomSheetSlide(slideOffset: Float)
    fun onBottomSheetStateChanged(newState: Int)
    fun createRouteAction(
        pickUpLatLng: LatLng? = null,
        dropOffLatLng: LatLng? = null,
    )
}
