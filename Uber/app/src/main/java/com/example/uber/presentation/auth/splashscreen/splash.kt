package com.example.uber.presentation.auth.splashscreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.uber.R
import com.example.uber.core.common.Resource
import com.example.uber.domain.local.rider.model.Rider
import com.example.uber.presentation.auth.login.viewmodels.LoginViewModel
import com.example.uber.presentation.splash.SplashActivity
import com.example.uber.presentation.splash.viewmodel.RiderRoomViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class splash : Fragment() {

    private var auth: FirebaseAuth? = null
    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null
    private val RC_SIGN_IN = 2
    private lateinit var navController: NavController
    private val _loginViewModel: LoginViewModel by activityViewModels()
    private val riderRoomViewModel: RiderRoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun startIdentityIntent() {
        FirebaseApp.initializeApp(requireContext())
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onDestroyView() {
        auth = null
        oneTapClient = null
        signInRequest = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        startIdentityIntent()
        signInWithOneTap()
        observeUserLogin()
        observerUserExistsStatus()
    }

    private fun signInWithOneTap() {
        oneTapClient?.beginSignIn(signInRequest!!)
            ?.addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        RC_SIGN_IN,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (e: Exception) {
                    Log.e("OneTap", "Error starting One Tap Sign-In", e)
                }
            }
            ?.addOnFailureListener { e ->
                Log.e("OneTap", "One Tap Sign-In failed: ${e.localizedMessage}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val credential = oneTapClient?.getSignInCredentialFromIntent(data)
                if (credential != null) {
                    _loginViewModel.signIn(credential)
                } else {
                    Log.e("OneTap", "No ID token found!")
                }
            } catch (e: ApiException) {
                Log.e("OneTap", "One Tap Sign-In failed: ${e.localizedMessage}")
            }
        }
    }


    private fun observeUserLogin() {
        _loginViewModel.apply {
            user.observe(viewLifecycleOwner) {
                if (it != null) {
                    it.data?.email?.let { email ->
                        _loginViewModel.checkIfUserExists(email)
                    }
                } else {
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun observerUserExistsStatus() {
        _loginViewModel.apply {
            userExists.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val data = resource.data
                        data?.let {
                            if (it.riderId != UUID(0,0) && it.riderId != null) {
                                riderRoomViewModel.insertRider(Rider(it.riderId))
                                startActivity(Intent(requireContext(), SplashActivity::class.java))
                                requireActivity().finish()
                            } else {
                                val bundle = Bundle()
                                if (navController.currentDestination?.id == R.id.splash) {
                                    bundle.putString(
                                        "displayName",
                                        _loginViewModel.user.value?.data?.displayName
                                    )
                                    navController.navigate(
                                        R.id.action_splash_to_registerDetailsFragment,
                                        bundle
                                    )
                                }
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

}