package com.example.uber.presentation.bottomSheet

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.example.uber.R
import com.example.uber.app.common.Resource
import com.example.uber.core.RxBus.RxBus
import com.example.uber.core.RxBus.RxEvent
import com.example.uber.core.enums.Markers
import com.example.uber.core.interfaces.IBottomSheetListener
import com.example.uber.core.utils.system.SystemInfo
import com.example.uber.presentation.viewModels.DropOffLocationViewModel
import com.example.uber.presentation.viewModels.GeoCodeGoogleLocationViewModel
import com.example.uber.presentation.viewModels.PickUpLocationViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class BottomSheetManager(
    private val view: View,
    private val context: Context,
    private val bottomSheetListener: IBottomSheetListener,
    private val viewLifecycleOwner: LifecycleOwner,
    private val pickUpLocationViewModel: PickUpLocationViewModel,
    private val dropOffLocationViewModel: DropOffLocationViewModel,
) {
    private val bottomSheet: View = view.findViewById(R.id.bottom_sheet)
    private val bottomSheetContentll: LinearLayout by lazy { view.findViewById(R.id.llplan_your_ride) }
    private val whereTo: ConstraintLayout by lazy { view.findViewById(R.id.cl_where_to) }
    private val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    private var isPickupEtInFocus = false
    private var isDropOffEtInFocus = false
    private val et_pickup: EditText by lazy { view.findViewById(R.id.ti_pickup) }
    private val et_drop_off: EditText by lazy { view.findViewById(R.id.ti_drop_off) }
    private val bottomSheetHeading: TextView by lazy { view.findViewById(R.id.tv_bottom_sheet_heading) }
    private val tv_pin_location: TextView = view.findViewById(R.id.tv_pin_location)
    private val llSetLocationOnMap: LinearLayout by lazy { view.findViewById(R.id.ll_set_location_on_map) }
    private val btn_confirm_destination: AppCompatButton by lazy { view.findViewById(R.id.btn_confirm_destination) }

    init {
        setBottomSheetStyle()
        setupBottomSheetCallback()
        observePickUpLocationChanges()
        observeDropOffLocationChanges()
        setLocationOnMapLinearLayoutOnClickListener()
        setEditTextDropOffInFocus()
        editTextFocusChangeListener()
    }

    private fun setupBottomSheetCallback() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                handleStateChange(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheetListener.onBottomSheetSlide(slideOffset)
                fadeInOutBottomSheetContent(slideOffset)
                showPickUpDropOffContent(slideOffset)
            }
        })
    }


    private fun handleStateChange(newState: Int) {


        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED -> {
                (view.context as? Activity)?.dismissKeyboard()
            }
        }
    }


    private fun setBottomSheetStyle() {
        bottomSheet.layoutParams.height =
            (context.resources.displayMetrics.heightPixels * 0.95).toInt()
        bottomSheetBehavior.peekHeight =
            (context.resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = false
    }

    private fun fadeInOutBottomSheetContent(slideOffset: Float) {
        bottomSheetContentll.apply {
            if (slideOffset <= 0.5f) {
                when {
                    isPickupEtInFocus -> {
                        bottomSheetHeading.text = "Set your pickup spot"
                        btn_confirm_destination.text = "Confirm pickup"
                    }

                    isDropOffEtInFocus -> {
                        bottomSheetHeading.text =
                            context.getString(R.string.set_your_destination)
                        btn_confirm_destination.text = "Confirm destination"

                    }
                }
                visibility = View.VISIBLE
                alpha = 1 - slideOffset * 2
            } else {
                alpha = 0f
                visibility = View.GONE
            }
        }
    }

    private fun showPickUpDropOffContent(slideOffset: Float) {
        whereTo.apply {
            if (slideOffset >= 0.5f) {
                visibility = View.VISIBLE
                alpha = (slideOffset - 0.5f) * 2
            } else {
                alpha = 0f
                visibility = View.GONE
            }
        }
    }
    private fun Activity.dismissKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isAcceptingText)
            inputMethodManager.hideSoftInputFromWindow(et_pickup.windowToken, 0)
    }

    private fun observePickUpLocationChanges() {
        checkInternetConnection {
            with(pickUpLocationViewModel) {
                locationName.observe(viewLifecycleOwner) {
                    et_pickup.setText(it)
                    updateLocationText(it)
                }
            }
        }

    }

    private fun observeDropOffLocationChanges() {
        checkInternetConnection {
            with(dropOffLocationViewModel) {
                locationName.observe(viewLifecycleOwner) {
                    et_drop_off.setText(it)
                    updateLocationText(it)
                }
            }
        }
    }


    private fun setLocationOnMapLinearLayoutOnClickListener() {
        llSetLocationOnMap.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    private fun updateLocationText(longName: String) {
        tv_pin_location.text = longName
    }

    private fun checkInternetConnection(dispatcher: () -> Unit) {
        if (SystemInfo.CheckInternetConnection(context)) {
            try {
                dispatcher.invoke()
            } catch (e: Exception) {

            }
        }
    }

    private fun setEditTextDropOffInFocus() {
        et_drop_off.requestFocus()
    }

    fun hideBottomSheet(){
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun showBottomSheet(etAnnotationFocusListener:Markers?=null) {
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if(etAnnotationFocusListener != null) {
            when (etAnnotationFocusListener) {
                Markers.PICK_UP -> et_pickup.requestFocus()
                Markers.DROP_OFF -> et_drop_off.requestFocus()
            }
        }
    }

    fun bottomSheetBehaviour():Int{
        return bottomSheetBehavior.state
    }

    private fun editTextFocusChangeListener() {
        et_pickup.setOnFocusChangeListener { view, b ->
            if (b) {
                RxBus.publish(RxEvent.EventEditTextFocus(true,false))
                isPickupEtInFocus = true
                isDropOffEtInFocus = false
            }
        }
        et_drop_off.setOnFocusChangeListener { view, b ->
            RxBus.publish(RxEvent.EventEditTextFocus(false,true))
            if (b) {
                isPickupEtInFocus = false
                isDropOffEtInFocus = true
            }
        }
    }
}