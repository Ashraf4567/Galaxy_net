package com.galaxy.galaxynet

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



//        Handler(Looper.getMainLooper())
//            .postDelayed({
//
//                checkUserAuthentication()
//
//            }, 200)

        viewModel.isUserExist()
        viewModel.isExist.observe(viewLifecycleOwner) {
            if (it) {
                startHome()
            } else {
                startLoginFragment()
            }
        }
    }

    private fun startHome() {
        createToken()
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
    }

    private fun startLoginFragment() {
        viewModel.sessionManager.logout()
        auth.signOut()
        Log.e("test saved local", viewModel.sessionManager.getUserData()?.name.toString())
        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
    }

    private fun createToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
                Log.d("token value", token.toString())
                viewModel.saveToken(token.toString())
            }

        }

    }
}