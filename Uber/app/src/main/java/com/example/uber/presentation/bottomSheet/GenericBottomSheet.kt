package com.example.uber.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.uber.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenericBottomSheet: BottomSheetDialogFragment() {
    var customView: View? = null
    lateinit var bottomSheet: View
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dynamic_bottom_sheet, container, false)
        bottomSheet = view.findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        customView?.let {
            val containerView = view.findViewById<FrameLayout>(R.id.dynamicContentContainer)
            containerView.addView(it)
        }

        return view
    }

    companion object {
        fun newInstance(customView: View): GenericBottomSheet {
            val fragment = GenericBottomSheet()
            fragment.customView = customView
            return fragment
        }
    }
}