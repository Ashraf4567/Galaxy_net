package com.galaxy.galaxynet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.galaxy.galaxynet.databinding.ActivityMainBinding
import com.galaxy.galaxynet.ui.AddTaskFragment
import com.galaxy.galaxynet.ui.auth.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkUserType()

        askNotificationPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "YOUR_CHANNEL_ID",
                "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        var isManager: Boolean = false
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setupWithNavController(navController)


        viewModel.isManager.observe(this) {
//            if (it == null){
//                findNavController(R.id.container).navigate(R.id.loginFragment)
//                Log.d("check user type", "$it")
//            }
            isManager = it
        }
        bottomNavigationView.setOnItemSelectedListener { item ->

            handleBottomNavigation(item.itemId, isManager)

            true

        }


        // Add an OnDestinationChangedListener to the NavController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Check if the current destination is the signup or login fragment
            val isSignupOrLogin =
                destination.id == R.id.loginFragment
                        || destination.id == R.id.addAccountFragment || destination.id == R.id.splashFragment
            updateBottomNavBarVisibility(!isSignupOrLogin)

        }

        binding.addTaskBtn.setOnClickListener {
            showAddTaskSheet()
        }
        createToken()
    }

    private fun createToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
                Log.d("test token", token)
            }
        }
    }

    private fun handleBottomNavigation(itemId: Int, isManager: Boolean) {
        when (itemId) {
            R.id.profileFragment -> {

                val destinationId =
                    if (isManager) R.id.managerProfileFragment else R.id.profileFragment
                findNavController(R.id.container).navigate(destinationId)

            }

            else -> {
                findNavController(R.id.container).navigate(itemId)
            }
        }
    }


    private fun showAddTaskSheet() {
        val addTaskSheet = AddTaskFragment()
        addTaskSheet.onTaskAddedListener = AddTaskFragment.OnTaskSentListener {
            Snackbar.make(binding.root , "تم ارسال المهمه" , 2000).show()
        }
        addTaskSheet.show(supportFragmentManager , "")
    }

    private fun updateBottomNavBarVisibility(visible: Boolean) {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        if (visible) {
            bottomNavView.visibility = View.VISIBLE
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.addTaskBtn.visibility = View.VISIBLE
        } else {
            bottomNavView.visibility = View.GONE
            binding.bottomAppBar.visibility = View.GONE
            binding.addTaskBtn.visibility = View.GONE
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
