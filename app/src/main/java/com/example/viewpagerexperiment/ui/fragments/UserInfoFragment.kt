package com.example.viewpagerexperiment.ui.fragments

import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.data.user
import com.example.viewpagerexperiment.databinding.UserInfoFragmentBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment

class UserInfoFragment : BaseFragment<UserInfoFragmentBinding>(R.layout.user_info_fragment) {

    override fun onViewCreated(binding: UserInfoFragmentBinding) {
        super.onViewCreated(binding)
        binding.apply {
            user().apply {
                userName.text = firstName
                lastNameView.text = lastName
                mobileNumber.text = phone
            }
        }
    }
}