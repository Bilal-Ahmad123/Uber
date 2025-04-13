package com.example.uber.presentation.riderpresentation.bottomSheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uber.R
import com.example.uber.core.enums.SheetState
import com.example.uber.core.utils.system.SystemInfo
import com.example.uber.data.remote.api.googleMaps.models.SuggetionsResponse.Prediction
import com.example.uber.data.remote.api.mapBox.models.SuggestionResponse.PlaceDetail
import com.example.uber.databinding.BottomSheetWhereToBinding
import com.example.uber.presentation.adapter.PlaceSuggestionAdapter
import com.example.uber.presentation.animation.AnimationManager
import com.example.uber.presentation.riderpresentation.viewModels.GoogleViewModel
import com.example.uber.presentation.riderpresentation.viewModels.MapAndSheetsSharedViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.jakewharton.rxbinding.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BottomSheetManager : Fragment(R.layout.bottom_sheet_where_to) {
    private lateinit var placeSuggestionAdapter: PlaceSuggestionAdapter
    private lateinit var binding: BottomSheetWhereToBinding
    private var bottomSheet: LinearLayout? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private val googleViewModel: GoogleViewModel by activityViewModels<GoogleViewModel>()
    private val sharedViewModel : MapAndSheetsSharedViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        initializeSheetProperties()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetWhereToBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }


    private fun initializeSheetProperties() {
        setUpBottomSheet()
        setLocationOnMapLinearLayoutOnClickListener()
        setEditTextDropOffInFocus()
        editTextFocusChangeListener()
        observePlacesSuggestions()
        debounce()
        setUpRecyclerViewAdapter()
        sharedViewModel.setCurrentOpenedSheet(SheetState.PICKUP_SHEET)
        sharedViewModel.setDropOffInputInFocus(true)
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
        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                handleStateChange(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
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
                if (sharedViewModel.pickUpInputInFocus.value!!)
                    requestEditTextPickUpFocus()
                else if(sharedViewModel.dropOffInputInFocus.value!!)
                    requestEditTextDropOffFocus()
            }
        }
    }

    private fun hideKeyBoard() {
        if (sharedViewModel.pickUpInputInFocus.value!!) {
            binding.tiPickup.let { view ->
                val imm =
                    context
                        ?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } else if(sharedViewModel.dropOffInputInFocus.value!!) {
            binding.tiDropOff.let { view ->
                val imm =
                    context
                        ?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun setBottomSheetStyle() {
        bottomSheet = requireActivity().findViewById<LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
        bottomSheet?.layoutParams?.height =
            (requireContext().resources.displayMetrics.heightPixels * 0.95).toInt()
        bottomSheetBehavior?.peekHeight =
            (requireContext().resources.displayMetrics.heightPixels * 0.32).toInt()
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior?.isHideable = false
        bottomSheetBehavior?.isDraggable = true
    }

    private fun fadeInOutBottomSheetContent(slideOffset: Float) {
        binding.llplanYourRide.apply {
            if (slideOffset <= 0.5f) {
                when {
                    sharedViewModel.pickUpInputInFocus.value!! -> {
                        binding.tvBottomSheetHeading.text = "Set your pickup spot"
                        binding.btnConfirmDestination.text = "Confirm pickup"
                    }

                    sharedViewModel.dropOffInputInFocus.value!! -> {
                        binding.tvBottomSheetHeading.text =
                            context.getString(R.string.set_your_destination)
                        binding.btnConfirmDestination.text = "Confirm destination"
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
        binding.clWhereTo.apply {
            if (slideOffset >= 0.5f) {
                visibility = View.VISIBLE
                alpha = (slideOffset - 0.5f) * 2
            } else {
                alpha = 0f
                visibility = View.GONE
            }
        }
    }

    private fun observePickUpLocationChanges() {
        with(googleViewModel) {
            pickUpLocationName.observe(viewLifecycleOwner) {
                binding.tiPickup.setText(it)
                updateLocationText(it)
            }
        }

    }

    private fun observeDropOffLocationChanges() {
        with(googleViewModel) {
            dropOffLocationName.observe(viewLifecycleOwner) {
                binding.tiDropOff.setText(it)
                updateLocationText(it)
            }
        }
    }


    private fun setLocationOnMapLinearLayoutOnClickListener() {
        binding.llSetLocationOnMap.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    private fun updateLocationText(longName: String) {
        binding.tvPinLocation.text = longName
    }

    private fun setEditTextDropOffInFocus() {
        sharedViewModel.apply {
            pickUpInputInFocus.observe(viewLifecycleOwner){
                if (it){
                    requestEditTextPickUpFocus()
                }
            }

            dropOffInputInFocus.observe(viewLifecycleOwner){
                if (it){
                    requestEditTextDropOffFocus()
                }
            }
        }
    }


    private fun hideBottomSheet() {
        bottomSheetBehavior?.isHideable = true
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun editTextFocusChangeListener() {
        binding.tiPickup.setOnFocusChangeListener { view, b ->
            if (b) {
                sharedViewModel.setPickUpInputInFocus(true)
            }
        }
        binding.tiDropOff.setOnFocusChangeListener { view, b ->
            if (b) {
                sharedViewModel.setDropOffInputInFocus(true)
            }
        }
    }

    private fun getPlacesSuggestions(place: String) {
        googleViewModel.getPlacesSuggestion(place)
    }

    private fun observePlacesSuggestions() {
        with(googleViewModel) {
            placesSuggestion.observe(viewLifecycleOwner) {
                val searchedResults: MutableList<PlaceDetail> =
                    extractSearchedResults(it.data?.predictions)
                showSuggestedPlacesOnBottomSheet(searchedResults)
            }
        }
    }

    private fun setUpRecyclerViewAdapter() {
        placeSuggestionAdapter = PlaceSuggestionAdapter { place ->
            executeSearchedPlace(place)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = placeSuggestionAdapter
        setItemRecyclerViewItemDivider()
    }

    private var searchedResults: MutableList<PlaceDetail> = mutableListOf()
    private fun showSuggestedPlacesOnBottomSheet(searchedResults: MutableList<PlaceDetail>) {
        placeSuggestionAdapter.submitList(searchedResults)
        this.searchedResults = searchedResults
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetBehavior = null
        sharedViewModel.cleanData()
        binding.tiPickup.onFocusChangeListener = null
        binding.tiDropOff.onFocusChangeListener = null
    }
    private fun extractSearchedResults(suggestions: List<Prediction>?): MutableList<PlaceDetail> {
        val searchedResults: MutableList<PlaceDetail> = mutableListOf()
        suggestions?.forEach {
            searchedResults.add(
                PlaceDetail(
                    name = it.description,
                    fullAddress = it.structured_formatting.main_text,
                    googleId = it.place_id
                )
            )
        }
        return searchedResults
    }

    private fun executeSearchedPlace(place: PlaceDetail) {
        googleViewModel.retrieveSuggestedPlaceDetail(place.googleId)
    }


    private fun observeSuggestedPlaceDetail() {
        googleViewModel.apply {
            retrieveSuggestedPlaceDetail.observe(viewLifecycleOwner) {
                if(it != null) {
                    if (sharedViewModel.pickUpInputInFocus.value!!) {
                        createRouteAndHideSheet(pickUpLatLng = LatLng(it[0], it[1]))
                    } else {
                        createRouteAndHideSheet(dropOffLatLng = LatLng(it[0], it[1]))
                    }
                }
            }
            cleanRetreiveSuggestedData()
        }
    }

    private fun setItemRecyclerViewItemDivider() {
        val dividerItemDecoration = MaterialDividerItemDecoration(
            requireContext(),
            MaterialDividerItemDecoration.VERTICAL
        ).apply {
            dividerInsetEnd = 10
            dividerInsetStart = 16

        }
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun translateOnXAxis() {
        AnimationManager.animateToEndOfScreenAndScale(binding.lineView, context = requireContext())
    }

    private fun pickUpLocationDebounce() {
        RxTextView.textChanges(binding.tiPickup).debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                googleViewModel.getPlacesSuggestion(it.toString())
                getPlacesSuggestions(it.toString())
                translateOnXAxis()
            }
    }

    private fun dropOffLocationDebounce() {
        RxTextView.textChanges(binding.tiDropOff).debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                getPlacesSuggestions(it.toString())
                translateOnXAxis()
            }
    }

    private fun confirmDestinationBtnClickListener() {
        binding.btnConfirmDestination.setOnClickListener {
            createRouteAndHideSheet()
        }
    }


    private fun createRoute(
        pickUpLatLng: LatLng? = null,
        dropOffLatLng: LatLng? = null
    ) {
        sharedViewModel.setIsDestinationConfirmBtnClicked(true)
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
            googleViewModel.setPickUpLocationName(it.latitude, it.longitude)
        }
        dropOffLatLng?.let {
            googleViewModel.setDropOffLocationName(it.latitude, it.longitude)
        }
    }

    fun requestEditTextDropOffFocus() {
        binding.tiDropOff.requestFocus()
        showKeyBoardOnBottomsheetExpand()
    }

    private fun requestEditTextPickUpFocus() {
        binding.tiPickup.requestFocus()
        showKeyBoardOnBottomsheetExpand(binding.tiPickup)
    }


    private fun showKeyBoardOnBottomsheetExpand(editText: EditText = binding.tiDropOff) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

    }

}