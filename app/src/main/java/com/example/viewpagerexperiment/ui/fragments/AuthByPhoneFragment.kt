package com.example.viewpagerexperiment.ui.fragments

import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.utils.runOnUiThread
import com.example.utils.toast
import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.data.UserConfig
import com.example.viewpagerexperiment.databinding.AuthByPhoneFragmentBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator

class AuthByPhoneFragment :
    BaseFragment<AuthByPhoneFragmentBinding>(R.layout.auth_by_phone_fragment) {

    private var fragments = ArrayList<BaseFragment<*>>()

    init {
        fragments.add(AuthIndividualFragment())
        fragments.add(AuthCompanyFragment())
    }

    inner class Adapter(manager: FragmentManager, cycle: Lifecycle) :
        FragmentStateAdapter(manager, cycle) {
        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        override fun getItemId(position: Int): Long {
            return fragments[position].hashCode().toLong()
        }

        override fun containsItem(itemId: Long): Boolean {
            return fragments.find { it.hashCode().toLong() == itemId } != null
        }
    }

    private lateinit var myAdapter: Adapter

    override fun onViewCreated(binding: AuthByPhoneFragmentBinding) {
        super.onViewCreated(binding)
        hasActionBar = true
        runOnUiThread(100) {
            hasBackButton = UserConfig.firstTime
        }
        if (UserConfig.firstTime) {
            UserConfig.setNotFirst()
        }
        actionBarTitle = "Authorization"
        binding.apply {
            myAdapter = Adapter(childFragmentManager, lifecycle)

            viewPager.apply {
                adapter = myAdapter
                currentItem = 0

                TabLayoutMediator(tabLayout, this) { tab, pos ->
                    //Just empty
                    tab.text = if (pos == 0) "Individual" else "Company"

                    val tabView = tab.view
                    val field = tabView.javaClass.getDeclaredField("textView")
                    field.isAccessible = true
                    val textView = field.get(tabView) as TextView
                    textView.isAllCaps = false
                }.attach()
            }
        }
    }
}