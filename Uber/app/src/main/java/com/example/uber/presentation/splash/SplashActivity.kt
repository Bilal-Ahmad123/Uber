package com.example.uber.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.uber.R
import com.example.uber.core.common.Resource
import com.example.uber.presentation.auth.AuthActivity
import com.example.uber.presentation.auth.login.viewmodels.LoginViewModel
import com.example.uber.presentation.riderpresentation.MainActivity
import com.example.uber.presentation.splash.viewmodel.RiderRoomViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel>()
    private val riderRoomViewModel: RiderRoomViewModel by viewModels<RiderRoomViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkIfUserLoggedIn()
    }

    private fun checkIfUserLoggedIn() {
        FirebaseApp.initializeApp(this)
        val user = FirebaseAuth.getInstance()?.currentUser
        if (user != null) {
            getUser()
            lifecycleScope.launch {
                riderRoomViewModel.apply {
                    rider.collectLatest {
                        when (it) {
                            is Resource.Success -> {
                                val rider = it.data
                                if (rider?.riderId != null) {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            AuthActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }
                            else -> Unit
                        }

                    }
                }
            }
        } else {
            startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            finish()
        }
    }

    private fun getUser() {
        riderRoomViewModel.getRider()
    }
}