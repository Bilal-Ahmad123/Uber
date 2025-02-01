package com.example.uber.presentation.auth.register.ui

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.example.uber.R
import com.example.uber.databinding.FragmentRegisterDetailsBinding

class RegisterDetailsFragment : Fragment() {
    private lateinit var _navController: NavController
    private lateinit var _binding:FragmentRegisterDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterDetailsBinding.inflate(inflater,container,false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateNameFields()
    }

    private fun populateNameFields(){
        val displayName = arguments?.getString("displayName")
        val (fname, lname) = displayName?.split(" ") ?: listOf("", "")
        _binding.etFirstName.editText?.text =  Editable.Factory.getInstance().newEditable(fname)
        _binding.etLastName.editText?.text =  Editable.Factory.getInstance().newEditable(lname)
    }

}