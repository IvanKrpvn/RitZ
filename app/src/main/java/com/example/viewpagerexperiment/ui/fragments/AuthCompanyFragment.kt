package com.example.viewpagerexperiment.ui.fragments

import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.data.UserConfig
import com.example.viewpagerexperiment.databinding.AuthCompanyFragmentBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment

class AuthCompanyFragment :
    BaseFragment<AuthCompanyFragmentBinding>(R.layout.auth_company_fragment) {
    
    override fun onViewCreated(binding: AuthCompanyFragmentBinding) {
        super.onViewCreated(binding)
        hasBackButton = UserConfig.firstTime
        actionBarTitle = "Authorization"
    }
    
}