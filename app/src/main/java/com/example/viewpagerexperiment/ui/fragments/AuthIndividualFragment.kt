package com.example.viewpagerexperiment.ui.fragments

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.utils.*
import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.data.AuthServerController
import com.example.viewpagerexperiment.data.ServerRequestResult
import com.example.viewpagerexperiment.data.UserConfig
import com.example.viewpagerexperiment.databinding.AuthIndividualFragmentBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthIndividualFragment :
    BaseFragment<AuthIndividualFragmentBinding>(R.layout.auth_individual_fragment) {

    override fun onResume() {
        super.onResume()
        runOnUiThread(300) {
            showKeyboard(requireBinding().editLayout.editText,true)
        }
    }

    private fun sendCode(phone: String) {
        showProgressBar(true)
        lifecycleScope.launch(Dispatchers.IO) {
            AuthServerController.getInstance().sendVerificationCode(phone, false) {
                launchOnMain {
                    showProgressBar(false)
                    when (it) {
                        is ServerRequestResult.Error -> {
                            requireBinding().editLayout.applyError(it.errorDescription)
                        }
                        is ServerRequestResult.Success -> {
                            openFragment(VerifyPhoneFragment(it.result, phone, true))
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(binding: AuthIndividualFragmentBinding) {
        super.onViewCreated(binding)

        hasBackButton = UserConfig.firstTime
        actionBarTitle = "Authorization"

        binding.apply {

            getCodeButton.setStateIsActive(false)
            editLayout.applyPhoneEditWatcher()
            editLayout.applyTextHintColor()

            var ignoreType = false
            editLayout.editText!!.addTextChangedListener {
                if (ignoreType) return@addTextChangedListener
                val text = it.toString()
                getCodeButton.setStateIsActive(checkPhoneIsValid(text))
                ignoreType = false
            }

            getCodeButton.setOnClickListener {
                val phone = editLayout.editText!!.text.toString()
                if (checkPhoneIsValid(phone)) {
                    sendCode(phone)
                } else {
                    editLayout.applyError("The phone number must be entered correctly")
                }
            }
            signUpButton.setOnClickListener {
                openFragment(AuthSplashChooseFragment())
            }
        }
    }
}