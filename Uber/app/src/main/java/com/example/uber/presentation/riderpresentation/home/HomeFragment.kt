package com.example.uber.presentation.riderpresentation.home

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.uber.R
import com.example.uber.core.common.Constants_API
import com.example.uber.core.utils.FetchLocation
import com.example.uber.core.utils.permissions.PermissionManagers
import com.example.uber.databinding.FragmentHomeBinding
import com.example.uber.domain.remote.socket.location.model.UpdateLocation
import com.example.uber.presentation.riderpresentation.viewModels.LocationViewModel
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
    private val socketViewModel: SocketViewModel by activityViewModels<SocketViewModel>()
    private val riderRoomViewModel: RiderRoomViewModel by viewModels()
    private val locationViewModel : LocationViewModel  by activityViewModels<LocationViewModel>()
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
        observeSocketConnected()
    }

    private fun observeRider() {
        lifecycleScope.launch {
            riderRoomViewModel.apply {
                rider.collectLatest {
                    if (it.data?.riderId != null && it.data.riderId != UUID(0, 0))
                        connectToSocket(it.data.riderId)
                        socketViewModel.observeConnectedToSocket()
                }
            }
        }
    }

    private fun connectToSocket(riderId: UUID) {
        socketViewModel.connectToSocket("${Constants_API.SOCKET_API}?riderId=${riderId}")
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

    private fun startObservingNearbyDrivers(){
        locationViewModel.startObservingDriversLocation()
        locationViewModel.observeDriversLocations()
    }

    private fun observeSocketConnected(){
        viewLifecycleOwner.lifecycleScope.launch {
            with(socketViewModel){
                socketConnected.collectLatest {
                    if(it){
                        startObservingNearbyDrivers()
                        sendContinuousLocationUpdates()
                    }
                }
            }
        }
    }


}