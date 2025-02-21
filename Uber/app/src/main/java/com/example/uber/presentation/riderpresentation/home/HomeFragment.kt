package com.example.uber.presentation.riderpresentation.home

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.uber.R
import com.example.uber.core.utils.FetchLocation
import com.example.uber.core.utils.permissions.PermissionManagers
import com.example.uber.databinding.FragmentHomeBinding
import com.example.uber.presentation.riderpresentation.viewModels.SocketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val socketViewModel: SocketViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setUpWhereToClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectToSocket()
        sendContinuousLocationUpdates()
    }

    private fun connectToSocket() {
        socketViewModel.connectToSocket("ws://192.168.18.65:5213/riderhub")
    }

    private fun setUpWhereToClickListener() {
        binding.llWhereTo.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_pickUpMapFragment)
        }
    }

    private fun sendContinuousLocationUpdates() {
        Log.d("HomeFragment", "Location")
       lifecycleScope.launch {
            FetchLocation.getLocationUpdates(requireContext()).collectLatest {
                Log.d("HomeFragment", "Location: $it")
                socketViewModel.sendMessage(
                    com.example.uber.data.local.models.Location(
                        it.latitude,
                        it.longitude
                    )
                )
            }
        }
    }

    private fun checkLocationPermission(rationale: String?, onGranted: () -> Unit) {
        PermissionManagers.requestPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            if (it) {
                onGranted.invoke()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

}