package com.galaxy.galaxynet.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentManagerProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagerProfileFragment : Fragment() {

    lateinit var binding: FragmentManagerProfileBinding

    //manger -> menu (my account - add tasks request - employees list - add Account)
    val sectionsList = listOf<String>("حسابي", "طلبات اضافه المهام", "قائمه الموظفين", "اضافه حساب")
    private val sectionsAdapter = ManagerSectionsAdapter(sectionsList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManagerProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

    }

    private fun initViews() {
        binding.sectionsRecycler.adapter = sectionsAdapter

        sectionsAdapter.onSectionClickListener =
            ManagerSectionsAdapter.OnSectionClickListener {
                handleSectionsNavigation(it)
            }
    }

    private fun handleSectionsNavigation(it: String) {
        when (it) {
            "اضافه حساب" -> {
                findNavController().navigate(R.id.action_managerProfileFragment_to_addAccountFragment)
            }
        }
    }

}