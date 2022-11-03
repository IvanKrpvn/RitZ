package com.example.viewpagerexperiment.ui.fragments

import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.databinding.AuthSplashFragmentBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment

class AuthSplashChooseFragment :
    BaseFragment<AuthSplashFragmentBinding>(R.layout.auth_splash_fragment) {

    override fun onViewCreated(binding: AuthSplashFragmentBinding) {
        hasBackButton = true

        binding.apply {
            individual.setOnClickListener {
                openFragment(EnterPhoneFragment())
            }
        }
    }
}