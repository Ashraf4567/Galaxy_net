package com.galaxy.galaxynet

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.ui.auth.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()
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
        // In your Application class or first Activity
        Log.e("test saved local", viewModel.sessionManager.getUserData()?.name.toString())
        Firebase.firestore.persistentCacheIndexManager?.apply {
            // Indexing is disabled by default
            enableIndexAutoCreation()
        } ?: println("indexManager is null")

        Handler(Looper.getMainLooper())
            .postDelayed({
                checkUserAuthentication()
            }, 500)
    }

    private fun checkUserAuthentication() {


        if (viewModel.sessionManager.getUserData()?.name == null) {
            // User is already authenticated, proceed to MainActivity

            startLoginFragment()
        } else {
            // User is not authenticated, navigate to LoginActivity
            startHome()


        }
    }

    private fun startHome() {
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
    }

    private fun startLoginFragment() {
        Log.e("test saved local", viewModel.sessionManager.getUserData()?.name.toString())
        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
    }
}