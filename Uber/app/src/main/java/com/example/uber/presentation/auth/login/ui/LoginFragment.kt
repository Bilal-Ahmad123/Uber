package com.example.uber.presentation.auth.login.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.uber.R
import com.example.uber.databinding.FragmentLoginBinding
import com.example.uber.presentation.MainActivity
import com.example.uber.presentation.auth.login.viewmodels.LoginViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var _binding: FragmentLoginBinding
    private lateinit var signInRequest: BeginSignInRequest
    private val RC_SIGN_IN = 2
    private lateinit var navController: NavController
    private val _loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        _binding.signInWithGoogle.setOnClickListener {
            signInWithOneTap()
        }
        observeUserLogin()
    }

    private fun signInWithOneTap() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
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
            .addOnFailureListener { e ->
                Log.e("OneTap", "One Tap Sign-In failed: ${e.localizedMessage}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
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
                    Toast.makeText(
                        requireContext(),
                        "Signed in as ${it.data?.displayName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (it.data != null && it.data.metadata?.creationTimestamp?.equals(it.data.metadata!!.lastSignInTimestamp)!!) {
                        navController.navigate(R.id.action_loginFragment_to_registerDetailsFragment)
                    } else {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    }
                } else {
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}