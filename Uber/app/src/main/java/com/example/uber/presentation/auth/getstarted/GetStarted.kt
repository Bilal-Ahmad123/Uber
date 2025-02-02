package com.example.uber.presentation.auth.getstarted

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.uber.R
import com.example.uber.databinding.FragmentGetStartedBinding


class GetStarted : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentGetStartedBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGetStartedBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        onGetStartedBtnClickListener()
    }

    private fun onGetStartedBtnClickListener() {
        _binding?.getStarted?.setOnClickListener {
            navController.navigate(R.id.action_getStarted_to_loginFragment)
        }
    }

}