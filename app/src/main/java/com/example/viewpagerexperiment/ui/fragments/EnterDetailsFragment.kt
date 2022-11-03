package com.example.viewpagerexperiment.ui.fragments

import androidx.lifecycle.lifecycleScope
import com.example.utils.*
import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.data.AuthServerController
import com.example.viewpagerexperiment.databinding.EnterDetialsFragmentBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment
import kotlinx.coroutines.launch

class EnterDetailsFragment :
    BaseFragment<EnterDetialsFragmentBinding>(R.layout.enter_detials_fragment) {

    override fun onResume() {
        super.onResume()
        showKeyboard(requireBinding().emailView.editText!!, true)
    }

    private fun saveUserData(email: String, userName: String, lastName: String) {
        showProgressBar(true)
        lifecycleScope.launch {
            AuthServerController.getInstance()
                .saveUserInfo(email, userName, lastName) { success, updateRecords ->
                    showProgressBar(false)
                    if (success) {
                        toast("User info saved! $updateRecords")
                        openFragment(UserInfoFragment())
                    } else {
                        toast("Error while saving!")
                    }
                }
        }
    }

    override fun onViewCreated(binding: EnterDetialsFragmentBinding) {
        super.onViewCreated(binding)
        actionBarTitle = "Enter your details"

        binding.apply {
            emailView.latinNotAllowed()
            firstNameView.latinNotAllowed()
            lastNameView.latinNotAllowed()

            createAccountButton.setOnClickListener {
                val email = emailView.editText!!.text.toString()
                val userName = firstNameView.editText!!.text.toString()
                val lastName = lastNameView.editText!!.text.toString()

                if (checkEmailValid(email)) {
                    return@setOnClickListener
                }
                if (emailView.checkEmpty()) {
                    return@setOnClickListener
                }
                if (firstNameView.checkEmpty()) {
                    return@setOnClickListener
                }
                if (lastNameView.checkEmpty()) {
                    return@setOnClickListener
                }
                saveUserData(email, userName, lastName)
            }
        }
    }
}