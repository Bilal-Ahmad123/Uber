package com.example.uber.presentation.auth.register.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.uber.R
import com.example.uber.databinding.FragmentTermsAndReviewBinding
import com.example.uber.presentation.bottomSheet.GenericBottomSheet
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TermsAndReviewFragment : Fragment() {

    private var _binding: FragmentTermsAndReviewBinding? = null
    private lateinit var navController: NavController
    private lateinit var button: MaterialButton
    private var _bottomSheet: GenericBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTermsAndReviewBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        backBtnClickListener()
        runLopperHandler()
        checkBoxListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bottomSheet = null

    }

    private fun backBtnClickListener() {
        _binding?.mbBack?.setOnClickListener {
            val customView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_start_over_content, null)
            button = customView.findViewById<MaterialButton>(R.id.mb_yes)
            yesBtnClickListener()
            _bottomSheet = GenericBottomSheet.newInstance(customView)
            _bottomSheet?.show(parentFragmentManager, _bottomSheet?.tag)
        }
    }

    private fun runLopperHandler() {
        lifecycleScope.launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                _binding?.progressBarCyclic?.visibility = View.GONE
                _binding?.rlMain?.visibility = View.VISIBLE
            }
        }
    }

    private fun checkBoxListener() {
        _binding?.ckCheckbox?.setOnClickListener {
            val checkBox = it as CheckBox
            _binding?.filledTonalButton?.isEnabled = checkBox.isChecked
            _binding?.filledTonalButton?.isClickable = checkBox.isChecked
        }
    }

    private fun yesBtnClickListener() {
        button.setOnClickListener {
            _bottomSheet?.dismiss()
            navController.navigate(R.id.action_termsAndReviewFragment_to_getStarted)
        }
    }


}