package com.example.viewpagerexperiment.ui.fragments

import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.utils.*
import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.data.AuthServerController
import com.example.viewpagerexperiment.data.ServerRequestResult
import com.example.viewpagerexperiment.databinding.FragmentAuthorizationByPhoneOfAnIndividualBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment
import com.example.viewpagerexperiment.ui.actionBar.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnterPhoneFragment :
    BaseFragment<FragmentAuthorizationByPhoneOfAnIndividualBinding>(R.layout.fragment_authorization_by_phone_of_an_individual) {
    private var code = ""
    private var isSuccess = false
    private var isLoading = false

    private fun showLoading(show: Boolean) {
        isLoading = show
        showProgressBar(show)
    }

    override fun onResume() {
        super.onResume()
        showKeyboard(requireBinding().editLayout.editText,true)
    }

    override fun onViewCreated(binding: FragmentAuthorizationByPhoneOfAnIndividualBinding) {
        super.onViewCreated(binding)
        actionBarTitle = "Enter your phone"
        hasActionBar = true

        binding.apply {
            button.setStateIsActive(false)
            editLayout.applyPhoneEditWatcher()

            personalCheckBox.setLinkedText(
                personalCheckBox.text.toString(),
                Pair("Privacy Policy",
                    "https://www.figma.com/file/S6xRQaGU85D6CjVq4ySvO1/Перевозки-_-UI-(ver2-c-клиентской-частью)?node-id=5682%3A20910"),
                Pair("terms of the User Agreement and the Offer *",
                    "https://www.google.co.uz/")
            )
            editLayout.editText!!.doOnTextChanged { text, start, before, count ->
                val isActive = text!!.length >= 12
                button.setStateIsActive(isActive)
            }
            button.setOnClickListener {
                if (isLoading) return@setOnClickListener
                val text = editLayout.editText!!.text.toString()

                if (text.length < 12) {
                    toast("Please enter valid phone number!")
                    return@setOnClickListener
                }
                if (!personalCheckBox.isChecked) {
                    toast("Agree with terms!")
                    return@setOnClickListener
                }
                if (!recieveNewsCheckBox.isChecked) {
                    toast("Agree with notifications!")
                    return@setOnClickListener
                }
                //Successfully checked
                sendCode(text)
            }
        }
    }

    companion object {
        fun sendCode(lifecycle: CoroutineScope,phoneNumber: String,result: (code: String,error: String) -> Unit) {
            lifecycle.launch(Dispatchers.IO) {
                AuthServerController.getInstance().sendVerificationCode(phoneNumber, true) {
                    launchOnMain {
                        when (it) {
                            is ServerRequestResult.Success -> {
                                result.invoke(it.result,"")
                            }
                            is ServerRequestResult.Error -> {
                                result.invoke("",it.errorDescription)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendCode(phoneNumber: String) {
        //Code sent ->
        showLoading(true)
        Companion.sendCode(lifecycleScope,phoneNumber) { code: String, error: String ->
            this.code = code
            showLoading(false)
            if (code.isNotEmpty()) {
                isSuccess = true
                openFragment(VerifyPhoneFragment(code, phoneNumber, true))
            } else {
                isSuccess = false
                requireBinding().editLayout.applyError(error)
                ErrorHandler.handleError(error)
            }
        }
    }
}