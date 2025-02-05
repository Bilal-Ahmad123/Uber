package com.example.uber.presentation.auth.register.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.uber.R
import com.example.uber.data.remote.api.backend.authentication.models.RequestModels.RiderRequest
import com.example.uber.databinding.FragmentTermsAndReviewBinding
import com.example.uber.presentation.auth.login.viewmodels.LoginViewModel
import com.example.uber.presentation.auth.register.viewmodels.RegisterViewModel
import com.example.uber.presentation.riderpresentation.MainActivity
import com.example.uber.presentation.riderpresentation.bottomSheet.GenericBottomSheet
import com.example.uber.presentation.splash.SplashActivity
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class TermsAndReviewFragment : Fragment() {

    private var _binding: FragmentTermsAndReviewBinding? = null
    private lateinit var navController: NavController
    private lateinit var button: MaterialButton
    private lateinit var noButton: MaterialButton
    private var _bottomSheet: GenericBottomSheet? = null
    private val _registerViewModel: RegisterViewModel by viewModels()
    private val _loginViewModel: LoginViewModel by activityViewModels<LoginViewModel>()

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
        nextBtnClickListener()
        observerCreateRiderResponse()
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
            noButton = customView.findViewById<MaterialButton>(R.id.mb_no)
            yesBtnClickListener()
            noBtnClickListener()
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

    private fun noBtnClickListener(){
        noButton.setOnClickListener {
            _bottomSheet?.dismiss()
        }
    }

    private fun nextBtnClickListener(){
        _binding?.filledTonalButton?.setOnClickListener {
            createRider()
        }
    }

    private fun createRider(){
        showProgressBar()
        _registerViewModel.createRider(createRiderRequestObject())
    }

    private fun showProgressBar(){
        _binding?.progressBarCyclic?.visibility = View.VISIBLE
        _binding?.rlMain?.visibility = View.GONE
    }

    private fun hideProgressBar(){
        _binding?.progressBarCyclic?.visibility = View.GONE
    }



    private fun observerCreateRiderResponse(){
        with(_registerViewModel){
            rider.observe(viewLifecycleOwner){
                if(it.data != null){
                    val intent = Intent(
                        requireContext(),
                        SplashActivity::class.java
                    )
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun createRiderRequestObject():RiderRequest{
        val contactNo = arguments?.getString("contactNo")
        val country = arguments?.getString("country")
        val user = _loginViewModel.user.value?.data
        val (firstName,lastName) = user?.displayName?.split(" ")!!
        return RiderRequest(user.email!!, firstName,lastName,country!!,contactNo!!)
    }


}