package com.example.uber.presentation.auth.register.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.uber.R
import com.example.uber.databinding.FragmentRegisterDetailsBinding
import com.example.uber.presentation.auth.validation.TextInputLayoutAdapter
import com.example.uber.presentation.riderpresentation.bottomSheet.GenericBottomSheet
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.Validator.ValidationListener
import com.mobsandgeeks.saripaar.annotation.NotEmpty


class RegisterDetailsFragment : Fragment(), ValidationListener {
    private var _binding: FragmentRegisterDetailsBinding? = null
    private lateinit var navController: NavController
    private lateinit var button: MaterialButton
    private var _bottomSheet: GenericBottomSheet? = null
    private lateinit var noButton: MaterialButton
    private var country: String? = "Pakistan"


    @NotEmpty(message = "First Name is required")
    private var _etFirstName: TextInputLayout? = null

    @NotEmpty(message = "Last Name is required")
    private var _etLastName: TextInputLayout? = null

    @NotEmpty(message = "Contact no is required")
    private  var _etContactNo: TextInputLayout ? = null
    private var validator: Validator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        validator = null
        _binding = null
        _bottomSheet = null
        _etFirstName = null
        _etLastName = null
        _etContactNo = null
        _etFirstName?.editText?.removeTextChangedListener(firstNameTextWatcher)
        _etLastName?.editText?.removeTextChangedListener(lastNameTextWatcher)
        _etContactNo?.editText?.removeTextChangedListener(contactNoTextWatcher)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterDetailsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        _etFirstName = _binding!!.etFirstName
        _etLastName = _binding!!.etLastName
        _etContactNo = _binding!!.etContatcNo
        validator = Validator(this);
        validator?.registerAdapter(TextInputLayout::class.java, TextInputLayoutAdapter())
        validator?.setValidationListener(this);
        populateNameFields()
        setNextButtonClickListener()
        backBtnClickListener()
        setCountryPickerChangeListener()
        addTextWatchers()
    }

    private fun addTextWatchers(){
        _binding?.etFirstName?.editText?.addTextChangedListener(firstNameTextWatcher)
        _binding?.etLastName?.editText?.addTextChangedListener(lastNameTextWatcher)
        _binding?.etContatcNo?.editText?.addTextChangedListener(contactNoTextWatcher)
    }

    private fun populateNameFields() {
        val displayName = arguments?.getString("displayName")
        val (fname, lname) = displayName?.split(" ") ?: listOf("", "")
        _binding!!.etFirstName.editText?.text = Editable.Factory.getInstance().newEditable(fname)
        _binding!!.etLastName.editText?.text = Editable.Factory.getInstance().newEditable(lname)
    }

    override fun onValidationSucceeded() {
        val bundle = Bundle()
        bundle.putString("contactNo", _binding?.etContatcNo?.editText?.text.toString())
        bundle.putString("country",country)
        navController.navigate(R.id.action_registerDetailsFragment_to_termsAndReviewFragment,bundle)
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.view
            val message = error.getCollatedErrorMessage(requireContext())
            if (view is TextInputLayout) {
                (view as TextInputLayout).error = message
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setNextButtonClickListener() {
        _binding?.filledTonalButton?.setOnClickListener {
            validator?.validate()
        }
    }

    private val firstNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _binding?.etFirstName?.error = null
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private val lastNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _binding?.etLastName?.error = null
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private val contactNoTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _binding?.etContatcNo?.error = null
        }
        override fun afterTextChanged(s: Editable?) {}
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

    private fun yesBtnClickListener() {
        button.setOnClickListener {
            _bottomSheet?.dismiss()
            navController.navigate(R.id.action_registerDetailsFragment_to_getStarted)
        }
    }

    private fun noBtnClickListener(){
        noButton.setOnClickListener {
            _bottomSheet?.dismiss()
        }
    }

    private fun setCountryPickerChangeListener(){
        _binding?.ccp?.setOnCountryChangeListener {
            country = it.name
        }
    }


}