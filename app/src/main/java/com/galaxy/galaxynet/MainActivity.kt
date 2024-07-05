package com.galaxy.galaxynet

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.galaxy.galaxynet.databinding.ActivityMainBinding
import com.galaxy.galaxynet.ui.AddTaskFragment
import com.galaxy.galaxynet.ui.auth.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
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
        FirebaseMessaging.getInstance().subscribeToTopic(com.galaxy.util.Constants.TOPIC)
        requestPermissions()
        Firebase.firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
            setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
        }

        Firebase.firestore.persistentCacheIndexManager?.apply {
            // Indexing is disabled by default
            enableIndexAutoCreation()
        } ?: println("indexManager is null")


        viewModel.checkUserType()
        askNotificationPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_channel",
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
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE
        )
    }


    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
