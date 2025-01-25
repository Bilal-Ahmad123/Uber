package com.example.uber.presentation.auth.login.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.credentials.GetCredentialResponse
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.credentials.CustomCredential
import com.example.uber.R
import com.example.uber.databinding.FragmentLoginBinding
import com.example.uber.presentation.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var _binding: FragmentLoginBinding


    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext());
        auth = FirebaseAuth.getInstance()



        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.signInWithGoogle.setOnClickListener{
            signIn()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentLoginBinding.inflate(inflater, container, false)
        return _binding.root
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: Exception) {
                Log.e("SignIn", "Google sign in failed",e)
                Toast.makeText(requireContext(), "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(requireContext(), "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                } else {
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


}