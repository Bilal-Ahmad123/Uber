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
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uber.R
import com.example.uber.core.RxBus.RxBus
import com.example.uber.core.RxBus.RxEvent
import com.example.uber.core.enums.Markers
import com.example.uber.core.interfaces.IActions
import com.example.uber.core.utils.system.SystemInfo
import com.example.uber.data.remote.models.mapbox.SuggestionResponse.PlaceDetail
import com.example.uber.data.remote.models.mapbox.SuggestionResponse.Suggestion
import com.example.uber.presentation.adapter.PlaceSuggestionAdapter
import com.example.uber.presentation.animation.AnimationManager
import com.example.uber.presentation.viewModels.MapboxViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.jakewharton.rxbinding.widget.RxTextView
import com.mapbox.mapboxsdk.geometry.LatLng
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


class BottomSheetManager(
    private val view: View,
    private val context: Context,
    private val pickUpMapFragmentActions: IActions,
    private val viewLifecycleOwner: LifecycleOwner,
    private val mapboxViewModel: MapboxViewModel,

    ) {
    private val bottomSheet: View = view.findViewById(R.id.bottom_sheet)
    private val bottomSheetContentll: LinearLayout by lazy { view.findViewById(R.id.llplan_your_ride) }
    private val whereTo: LinearLayout by lazy { view.findViewById(R.id.cl_where_to) }
    private val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    private var isPickupEtInFocus = false
    private var isDropOffEtInFocus = false
    private val et_pickup: EditText by lazy { view.findViewById(R.id.ti_pickup) }
    private val et_drop_off: EditText by lazy { view.findViewById(R.id.ti_drop_off) }
    private val bottomSheetHeading: TextView by lazy { view.findViewById(R.id.tv_bottom_sheet_heading) }
    private val tv_pin_location: TextView = view.findViewById(R.id.tv_pin_location)
    private val llSetLocationOnMap: LinearLayout by lazy { view.findViewById(R.id.ll_set_location_on_map) }
    private val btn_confirm_destination: AppCompatButton by lazy { view.findViewById(R.id.btn_confirm_destination) }
    private val recyclerView: RecyclerView by lazy { view.findViewById(R.id.recyclerView) }
    private val lineView: View by lazy { view.findViewById<View>(R.id.lineView) }
    private lateinit var placeSuggestionAdapter: PlaceSuggestionAdapter


    init {
        setUpBottomSheet()
        setUpObservers()
        setLocationOnMapLinearLayoutOnClickListener()
        setEditTextDropOffInFocus()
        editTextFocusChangeListener()
        observePlacesSuggestions()
        debounce()
        setUpRecyclerViewAdapter()
    }

    private fun debounce() {
        pickUpLocationDebounce()
        dropOffLocationDebounce()
    }

    private fun setUpBottomSheet() {
        setBottomSheetStyle()
        setupBottomSheetCallback()
        confirmDestinationBtnClickListener()
    }

    private fun setUpObservers() {
        observePickUpLocationChanges()
        observeDropOffLocationChanges()
        observeSuggestedPlaceDetail()
    }

    private fun setupBottomSheetCallback() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                handleStateChange(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                pickUpMapFragmentActions.onBottomSheetSlide(slideOffset)
                fadeInOutBottomSheetContent(slideOffset)
                showPickUpDropOffContent(slideOffset)
            }
        })
    }


    private fun handleStateChange(newState: Int) {

        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED -> {
                hideKeyBoard()
            }

            BottomSheetBehavior.STATE_HIDDEN -> {
                hideKeyBoard()
            }
            BottomSheetBehavior.STATE_EXPANDED -> {
                if (isPickupEtInFocus)
                    requestEditTextPickUpFocus()
                else
                    requestEditTextDropOffFocus()
            }
        }
    }

    private fun hideKeyBoard() {
        if (isPickupEtInFocus) {
            this.et_pickup?.let { view ->
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } else {
            this.et_drop_off?.let { view ->
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
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
            with(mapboxViewModel) {
                pickUpLocationName.observe(viewLifecycleOwner) {
                    et_pickup.setText(it)
                    updateLocationText(it)
                }
            }
        }

    }

    private fun observeDropOffLocationChanges() {
        checkInternetConnection {
            with(mapboxViewModel) {
                dropOffLocationName.observe(viewLifecycleOwner) {
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

    private fun hideBottomSheet() {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun showBottomSheet(etAnnotationFocusListener: Markers? = null) {
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (etAnnotationFocusListener != null) {
            when (etAnnotationFocusListener) {
                Markers.PICK_UP -> et_pickup.requestFocus()
                Markers.DROP_OFF -> et_drop_off.requestFocus()
            }
        }
    }

    fun bottomSheetBehaviour(): Int {
        return bottomSheetBehavior.state
    }

    private fun editTextFocusChangeListener() {
        et_pickup.setOnFocusChangeListener { view, b ->
            if (b) {
                RxBus.publish(RxEvent.EventEditTextFocus(true, false))
                isPickupEtInFocus = true
                isDropOffEtInFocus = false
            }
        }
        et_drop_off.setOnFocusChangeListener { view, b ->
            RxBus.publish(RxEvent.EventEditTextFocus(false, true))
            if (b) {
                isPickupEtInFocus = false
                isDropOffEtInFocus = true
                clearRecyclerViewAdapter()
            }
        }
    }


    private fun getPlacesSuggestions(place: String) {
        mapboxViewModel.getPlacesSuggestion(place)
    }

    private fun observePlacesSuggestions() {
        with(mapboxViewModel) {
            placesSuggestion.observe(viewLifecycleOwner) {
                val searchedResults: MutableList<PlaceDetail> =
                    extractSearchedResults(it.data?.suggestions)
                showSuggestedPlacesOnBottomSheet(searchedResults)
            }
        }
    }

    private fun setUpRecyclerViewAdapter() {
        placeSuggestionAdapter = PlaceSuggestionAdapter { place ->
            executeSearchedPlace(place)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = placeSuggestionAdapter
        setItemRecyclerViewItemDivider()
    }

    private var searchedResults: MutableList<PlaceDetail> = mutableListOf()
    private fun showSuggestedPlacesOnBottomSheet(searchedResults: MutableList<PlaceDetail>) {
        placeSuggestionAdapter.submitList(searchedResults)
        this.searchedResults = searchedResults
    }

    private fun extractSearchedResults(suggestions: List<Suggestion>?): MutableList<PlaceDetail> {
        val searchedResults: MutableList<PlaceDetail> = mutableListOf()
        suggestions?.forEach {
            searchedResults.add(
                PlaceDetail(
                    name = it.name,
                    fullAddress = it.place_formatted,
                    mapboxId = it.mapbox_id
                )
            )
        }
        return searchedResults
    }

    private fun executeSearchedPlace(place: PlaceDetail) {
        mapboxViewModel.retrieveSuggestedPlaceDetail(place.mapboxId)
    }

    private fun observeSuggestedPlaceDetail() {
        mapboxViewModel.run {
            retrieveSuggestedPlaceDetail.observe(viewLifecycleOwner) {
                if (isPickupEtInFocus) {
                    createRouteAndHideSheet(pickUpLatLng = LatLng(it[1], it[0]))
                } else {
                    createRouteAndHideSheet(dropOffLatLng = LatLng(it[1], it[0]))
                }
            }
        }
    }

    private fun setItemRecyclerViewItemDivider() {
        view.context.let {
            val dividerItemDecoration = MaterialDividerItemDecoration(
                it,
                MaterialDividerItemDecoration.VERTICAL
            ).apply {
                dividerInsetEnd = 10
                dividerInsetStart = 16
            }
            recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun translateOnXAxis() {
        AnimationManager.animateToEndOfScreenAndScale(lineView, context = context)
    }

    private fun pickUpLocationDebounce() {
        RxTextView.textChanges(et_pickup).debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                getPlacesSuggestions(it.toString())
                translateOnXAxis()
            }
    }

    private fun dropOffLocationDebounce() {
        RxTextView.textChanges(et_drop_off).debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                getPlacesSuggestions(it.toString())
                translateOnXAxis()
            }
    }

    private fun confirmDestinationBtnClickListener() {
        btn_confirm_destination.setOnClickListener {
            createRouteAndHideSheet()
        }
    }

    private fun createRoute(
        pickUpLatLng: LatLng? = null,
        dropOffLatLng: LatLng? = null
    ) {
        pickUpMapFragmentActions.createRouteAction(pickUpLatLng, dropOffLatLng)
    }

    private fun createRouteAndHideSheet(
        pickUpLatLng: LatLng? = null,
        dropOffLatLng: LatLng? = null
    ) {
        createRoute(pickUpLatLng, dropOffLatLng)
        hideBottomSheet()
        hideKeyBoard()
        updateLocationInViewModel(pickUpLatLng, dropOffLatLng)
    }

    private fun updateLocationInViewModel(
        pickUpLatLng: LatLng? = null,
        dropOffLatLng: LatLng? = null
    ) {
        pickUpLatLng?.let {
            mapboxViewModel.setPickUpLocationName(it.latitude, it.longitude)
        }
        dropOffLatLng?.let {
            mapboxViewModel.setDropOffLocationName(it.latitude, it.longitude)
        }
    }

    private fun clearRecyclerViewAdapter() {
        searchedResults.clear()
        placeSuggestionAdapter.notifyDataSetChanged()
    }

    fun requestEditTextDropOffFocus() {
        et_drop_off.requestFocus()
        showKeyBoardOnBottomsheetExpand()
    }

    private fun requestEditTextPickUpFocus() {
        et_pickup.requestFocus()
        showKeyBoardOnBottomsheetExpand(et_pickup)
    }

    private fun showKeyBoardOnBottomsheetExpand(editText: EditText = et_drop_off){
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

    }
    
}