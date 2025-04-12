package com.example.uber.presentation.riderpresentation.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.example.uber.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenericBottomSheet: BottomSheetDialogFragment() {
    var customView: View? = null
    lateinit var bottomSheet: View
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dynamic_bottom_sheet, container, false)
//        bottomSheet = view.findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        customView?.let {
            val containerView = view.findViewById<FrameLayout>(R.id.dynamicContentContainer)
            if (it.parent != null){
                (it.parent as ViewGroup).removeView(it)
            }
            containerView.addView(it)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(customView: View): GenericBottomSheet {
            val fragment = GenericBottomSheet()
            fragment.customView = customView
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)

            findViewById<View>(com.google.android.material.R.id.touch_outside)?.updateLayoutParams {
                height = 100
                width = 0
            }

            attributes = attributes.apply {
                gravity = Gravity.BOTTOM
            }
            setDimAmount(0.0f)
        }
    }
}