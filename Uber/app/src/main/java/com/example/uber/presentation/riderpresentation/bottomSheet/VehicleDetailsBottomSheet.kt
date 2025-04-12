package com.example.uber.presentation.riderpresentation.bottomSheet

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.uber.R
import com.example.uber.core.utils.Helper
import com.example.uber.core.utils.StringHelper
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.example.uber.presentation.riderpresentation.map.RouteCreationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import java.lang.ref.WeakReference


class VehicleDetailsBottomSheet(
    private val view: WeakReference<View>,
    private val context: Context
) {

    private val image : ImageView by lazy { view.get()!!.findViewById(R.id.vehicle_image) }
    private val vehicleName : TextView by lazy { view.get()!!.findViewById(R.id.vehicle_name) }
    private val maxSeats : TextView by lazy { view.get()!!.findViewById(R.id.max_seats) }
    private val fare : TextView by lazy { view.get()!!.findViewById(R.id.fare) }
    private val timeWillReachOn : TextView by lazy { view.get()!!.findViewById(R.id.time_of_arrival) }
    private val minsAway : TextView by lazy { view.get()!!.findViewById(R.id.mins_away) }
    private val vehicleDescription : TextView by lazy { view.get()!!.findViewById(R.id.vehicle_description) }
    private val chooseVehicle : MaterialButton by lazy { view.get()!!.findViewById(R.id.choose_vehicle) }


    private val bottomSheet: View by lazy {  view.get()!!.findViewById(R.id.vehicle_details_sheet)}
    private val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    private lateinit var googleMap: WeakReference<GoogleMap>


    private fun setBottomSheetStyle() {
        bottomSheet.layoutParams.height =
            (context.resources.displayMetrics.heightPixels * 0.55).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = false
    }

    fun bottomSheetBehaviour(): Int {
        return bottomSheetBehavior.state
    }


    fun showSheet(vehicle : NearbyVehicles){
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        initializeVehicleDetails(vehicle)
        adjustMapForBottomSheet(1f)
    }


    private fun initializeVehicleDetails(vehicle: NearbyVehicles){
            Glide.with(view.get()!!.context).load(vehicle.image)
                .into(image)
            vehicleName.text = vehicle.name
            maxSeats.text = vehicle.seats.toString()
            timeWillReachOn.text = StringHelper.calculateTimeWithVehicleTime(vehicle.time)
            minsAway.text = StringHelper.showTimeAway(vehicle.time)
            fare.text = StringHelper.showCurrency(vehicle.fare)
            vehicleDescription.text = vehicle.description
            chooseVehicle.text = "Choose "+vehicle.name
    }


    init {
        setBottomSheetStyle()
        hideSheet()
    }


    fun hideSheet() {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun adjustMapForBottomSheet(slideOffset: Float) {

       val  bounds = Helper.calculateBounds(RouteCreationHelper.latLngBounds) ?: return
        val totalSheetHeight = bottomSheet.height
        val mapPaddingBottom = (slideOffset * totalSheetHeight).toInt()

        googleMap.get()?.setPadding(0, 0, 0, mapPaddingBottom)
        googleMap.get()?.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                100
            )
        )
    }

    fun initializeGoogleMap(googleMap: WeakReference<GoogleMap>) {
        this.googleMap = googleMap
    }

}