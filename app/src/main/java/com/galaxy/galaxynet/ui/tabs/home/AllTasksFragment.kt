package com.galaxy.galaxynet.ui.tabs.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.galaxy.galaxynet.databinding.FragmentAllTasksBinding
import com.galaxy.galaxynet.ui.TasksViewModel
import com.galaxy.galaxynet.ui.controlPanel.EditTaskActivity
import com.galaxy.util.Constants
import com.galaxy.util.UiState
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllTasksFragment : Fragment() {
    lateinit var binding: FragmentAllTasksBinding
    private val adapter = AllTasksAdapter(null)
    private val viewModel: TasksViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllTasksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
        bindTabs()
    }

    var selectedTabIndex: Int = 0
    var selectedTabName: String = ""
    private fun bindTabs() {
        val tasksCategoriesList = listOf(
            "برمجه", "صيانه", "تركيب", "برمجه و تركيب", "حل مشكله", "توزيع", "تخطيط", "اخري"
        )
        tasksCategoriesList.forEach { categoryName ->
            val tab = binding.tapLayout.newTab()
            tab.text = categoryName
            binding.tapLayout.addTab(tab)
            var layoutParams = LinearLayout.LayoutParams(tab.view.layoutParams)
            layoutParams.marginEnd = 15
            layoutParams.topMargin = 15
            tab.view.layoutParams = layoutParams
        }

        binding.tapLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    selectedTabName = tab?.text.toString()
                    Log.d("tas tab", selectedTabName)
                    viewModel.getAllTasksByCategory(selectedTabName)
                    selectedTabIndex = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    selectedTabName = tab?.text.toString()
                    viewModel.getAllTasksByCategory(selectedTabName)
                }

            }
        )
        binding.tapLayout.getTabAt(0)?.select()
    }

    private fun initObservers() {
        viewModel.uIstate.observe(viewLifecycleOwner) {
            handleUISatate(it)
        }
        viewModel.allTasksLiveData.observe(viewLifecycleOwner) {
            Log.d("test by category", it?.size.toString())
            if (it?.size == 0) {
                binding.notFoundView.visibility = View.VISIBLE
                binding.allTasksAdapter.visibility = View.GONE
            } else {
                adapter.submitList(it)
                binding.notFoundView.visibility = View.GONE
                binding.allTasksAdapter.visibility = View.VISIBLE
            }

        }
        viewModel.messageLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {

        val swipeToRefreshView = binding.swiperefresh
        binding.allTasksAdapter.adapter = adapter
        adapter.onTakeTaskClickListener = AllTasksAdapter.OnTaskClickListener { task, position ->
            viewModel.takeTask(task.id!!)
            viewModel.getAllTasksByCategory(selectedTabName)
        }
        adapter.onDeleteTaskClickListener = AllTasksAdapter.OnTaskClickListener { task, position ->
            task.id?.let {
                viewModel.deleteTask(it)
                viewModel.getAllTasksByCategory(selectedTabName)
            }
        }

        adapter.onEditTaskClickListener = AllTasksAdapter.OnTaskClickListener { task, position ->
            val intent = Intent(requireActivity(), EditTaskActivity::class.java).apply {
                putExtra(Constants.TASK_ID, task.id.toString())
                Log.d("test id", task.id.toString())
                startActivity(this)
            }
        }

        swipeToRefreshView.setOnRefreshListener {
            viewModel.getAllTasksByCategory(selectedTabName)
            swipeToRefreshView.isRefreshing = false
        }

    }


    private fun handleUISatate(uiState: UiState) {
        when (uiState) {
            UiState.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
            }

            UiState.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            UiState.ERROR -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(activity, "حدث خطأ حاول مره اخري", Toast.LENGTH_SHORT).show()
            }
        }
    }


}