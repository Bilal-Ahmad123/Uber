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
import com.example.uber.domain.remote.location.model.UpdateLocation
import com.example.uber.presentation.riderpresentation.viewModels.SocketViewModel
import com.example.uber.presentation.splash.viewmodel.RiderRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val socketViewModel: SocketViewModel by viewModels()
    private val riderRoomViewModel: RiderRoomViewModel by viewModels()
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
        observeRider()
        riderRoomViewModel.getRider()
        socketViewModel.startObservingDriversLocation()
        socketViewModel.observeDriversLocations()
        sendContinuousLocationUpdates()
        observeDriverLocations()
    }

    private fun observeRider() {
        lifecycleScope.launch {
            riderRoomViewModel.apply {
                rider.collectLatest {
                    if(it.data?.riderId != null && it.data.riderId != UUID(0,0))
                    connectToSocket(it.data.riderId)
                }
            }
        }
    }

    private fun connectToSocket(riderId:UUID) {
        socketViewModel.connectToSocket("ws://192.168.18.65:5213/riderhub?riderId=${riderId}")
    }

    private fun setUpWhereToClickListener() {
        binding.llWhereTo.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_pickUpMapFragment)
        }
    }

    private fun sendContinuousLocationUpdates() {
        checkLocationPermission(null) {
            lifecycleScope.launch {
                FetchLocation.getLocationUpdates(requireContext()).collectLatest {
                    if (riderRoomViewModel.rider.value.data?.riderId != null) {
                        socketViewModel.sendMessage(
                            UpdateLocation(
                                riderRoomViewModel.rider.value.data!!.riderId,
                                it.longitude,
                                it.latitude
                            )
                        )
                    }

                }
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

    private fun observeDriverLocations(){
        lifecycleScope.launch {
            with(socketViewModel) {
                driverLocation.collectLatest {
                    Log.d("HomeFragment", "Driver location: $it")
                }
            }
        }
    }

}