package com.example.uber.presentation

import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.uber.R
import com.example.uber.core.utils.system.GPSCheck
import com.example.uber.core.utils.system.NetworkStateReceiver
import com.example.uber.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var currentNightMode: Int = 0
    private val ll_nointernet: View by lazy { findViewById(R.id.noInternetConnection) }
    private val ll_no_location_service: View by lazy { findViewById(R.id.ll_no_location_service) }
    private val iv_cross: View by lazy { findViewById(R.id.iv_cross) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }

        registerGPSListener()
        registerNetworkListener()
        createNavigation()
        checkForThemeChange()
        onCrossBtnClickListener()
    }

    private fun registerNetworkListener(){
        registerReceiver(NetworkStateReceiver(object : NetworkStateReceiver.NetworkStateReceiverListener {
            override fun networkAvailable() {
                disappearNoInternetConnection()
            }

            override fun networkUnavailable() {
                showNoInternetConnection()
            }
        }), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun registerGPSListener(){
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        intentFilter.addAction(Intent.ACTION_PROVIDER_CHANGED)
        registerReceiver(GPSCheck(object : GPSCheck.LocationCallBack {
            override fun turnedOn() {
                disappearNoLocationService()
            }

            override fun turnedOff() {
                showNoLocationService()
            }
        }), IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    private fun createNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.pickUpMapFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }

                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
            supportActionBar?.hide()
            binding.bottomNavigationView.setupWithNavController(navController)
        }
    }

    private fun checkForThemeChange() {
        val newNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (newNightMode != currentNightMode) {
            reload()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK != currentNightMode) {
            reload()
        }
    }

    private fun reload() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }


    private fun disappearNoInternetConnection(){
        ll_nointernet.visibility = View.GONE
    }

    private fun showNoInternetConnection(){
        ll_nointernet.visibility = View.VISIBLE
        ll_nointernet.bringToFront()
    }

    private fun disappearNoLocationService(){
        ll_no_location_service.visibility = View.GONE
    }

    private fun showNoLocationService(){
        ll_no_location_service.visibility = View.VISIBLE
    }

    private fun onCrossBtnClickListener(){
        iv_cross.setOnClickListener {
            disappearNoInternetConnection()
        }
    }

}
