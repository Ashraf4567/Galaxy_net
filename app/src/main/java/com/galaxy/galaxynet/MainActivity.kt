package com.galaxy.galaxynet

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.galaxy.galaxynet.databinding.ActivityMainBinding
import com.galaxy.galaxynet.ui.AddTaskFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setupWithNavController(navController)

        viewModel.checkUserType()

        var isManager: Boolean = false
        viewModel.isManager.observe(this) {
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
                destination.id == R.id.loginFragment || destination.id == R.id.addAccountFragment
            updateBottomNavBarVisibility(!isSignupOrLogin)

        }

        binding.addTaskBtn.setOnClickListener{
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
}