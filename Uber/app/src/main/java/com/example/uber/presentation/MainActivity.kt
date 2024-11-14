package com.example.uber.presentation

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.uber.R
import com.example.uber.core.utils.system.SystemInfo
import com.example.uber.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        createNavigation()
        checkForThemeChange()
        continuousBackgroundThread()
        onImageViewClickListener()
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

    private lateinit var _job:Job

    private fun continuousBackgroundThread() {
        _job = lifecycleScope.launch(Dispatchers.Default) {
            while (true) {
                if (!checkIfInternet() && !checkIfInternetVisible()) {
                    makeItVisibleOrDisappear(View.VISIBLE)
                } else if (checkIfInternet()) {
                    interactWithNoLocationService()
                    if (checkIfInternetVisible()) {
                        makeItVisibleOrDisappear(View.GONE)
                    }
                }
            }
        }
    }

    private suspend fun interactWithNoLocationService(){
        if (!isLocationServiceEnabled() && !isNoInternetConnectionVisible()) {
            makeNoLocationServiceDisappearOrAppear(View.VISIBLE)
        } else if (isLocationServiceEnabled() && isNoInternetConnectionVisible()) {
            makeNoLocationServiceDisappearOrAppear(View.GONE)
        }
    }

    private fun isLocationServiceEnabled(): Boolean {
        return SystemInfo.isLocationEnabled(this@MainActivity)
    }

    private fun checkIfInternetVisible(): Boolean {
        return ll_nointernet.visibility == View.VISIBLE
    }

    private suspend fun makeItVisibleOrDisappear(visibility: Int) {
        withContext(Dispatchers.Main) {
            ll_nointernet.visibility = visibility
        }
    }

    private fun checkIfInternet(): Boolean {
        return SystemInfo.CheckInternetConnection(this@MainActivity)
    }

    private fun isNoInternetConnectionVisible(): Boolean {
        return ll_no_location_service.visibility == View.VISIBLE
    }

    private suspend fun makeNoLocationServiceDisappearOrAppear(visibility: Int) {
        withContext(Dispatchers.Main) {
            ll_no_location_service.visibility = visibility
        }
    }

    private fun onImageViewClickListener(){
        iv_cross.setOnClickListener {
            if(_job.isActive){
                disappearNoInternetConnection()
                disposeBackgroundThread()
            }
        }
    }

    private fun disposeBackgroundThread(){
        _job.cancel()
    }

    private fun disappearNoInternetConnection(){
        ll_nointernet.visibility = View.GONE
    }
}
