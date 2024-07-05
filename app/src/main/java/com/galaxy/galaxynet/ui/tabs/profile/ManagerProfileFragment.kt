package com.galaxy.galaxynet.ui.tabs.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.galaxy.galaxynet.R
import com.galaxy.galaxynet.databinding.FragmentManagerProfileBinding
import com.galaxy.galaxynet.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagerProfileFragment : Fragment() {

    lateinit var binding: FragmentManagerProfileBinding
    private val viewModel: AuthViewModel by viewModels()

    //manger -> menu (my account - add tasks request - employees list - add Account)
    val sectionsList =
        listOf(
            "حسابي",
            "طلبات اضافه المهام",
            "قائمه الموظفين",
            "اضافه حساب",
            "اضافه نوع جهاز",
            "احصائيات الاجهزه",
            "سجل النقاط"
        )
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
        binding.logoutBtn.setOnClickListener {
            viewModel.auth.signOut()
            viewModel.sessionManager.logout()
            Toast.makeText(requireActivity(), "تم تسجيل الخروج", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_managerProfileFragment_to_loginFragment)
        }
        binding.icSendAlert.setOnClickListener {
            findNavController().navigate(R.id.action_managerProfileFragment_to_sendNotificationFragment)
        }
    }

    private fun handleSectionsNavigation(it: String) {
        when (it) {
            "اضافه حساب" -> {
                findNavController().navigate(R.id.action_managerProfileFragment_to_addAccountFragment)
            }

            "طلبات اضافه المهام" -> {
                findNavController().navigate(R.id.action_managerProfileFragment_to_tasksRequestsFragment)
            }

            "قائمه الموظفين" -> {
                findNavController().navigate(R.id.action_managerProfileFragment_to_employeesListFragment)
            }

            "اضافه نوع جهاز" -> {
                findNavController().navigate(R.id.action_managerProfileFragment_to_addDeviceTypeFragment)
            }
            "احصائيات الاجهزه" -> {
                findNavController().navigate(R.id.action_managerProfileFragment_to_devicesListFragment)
            }

            "حسابي" -> {
                findNavController().navigate(R.id.action_managerProfileFragment_to_profileFragment)
            }
            "سجل النقاط" ->{
                findNavController().navigate(R.id.action_managerProfileFragment_to_transactionsHistoryFragment)
            }

        }
    }

}