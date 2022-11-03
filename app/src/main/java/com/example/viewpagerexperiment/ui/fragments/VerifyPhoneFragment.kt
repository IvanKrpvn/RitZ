package com.example.viewpagerexperiment.ui.fragments

import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.utils.*
import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.data.AuthServerController
import com.example.viewpagerexperiment.databinding.FragmentVerifyPassowrdBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyPhoneFragment(
    val id: String,
    val phone: String,
    private val register: Boolean,
) :
    BaseFragment<FragmentVerifyPassowrdBinding>(R.layout.fragment_verify_passowrd) {

    private val resendEnabled: Boolean = true

    companion object {
        const val TIME_LIMIT = 30
    }

    private var resendTimes = 1

    private var timeLimit = TIME_LIMIT

    private var timeUpdateRunnable = {
        timeLimit -= 1
        updateTimeLimit()
        startTimeUpdate()
    }

    private fun updateTimeLimit() {
        val text = "You can resend the code in $timeLimit seconds"
        requireBinding().timeLimit.text = text
    }

    private fun resendTheCode() {
        val errorR = {
            toast("You have tried many times. Try again later!")
        }
        if (resendTimes == 5) {
            errorR.invoke()
        }
        showProgressBar(true)
        //Resend
        EnterPhoneFragment.sendCode(lifecycleScope, phone) { code, error ->
            showProgressBar(false)
            if (code.isNotEmpty()) {
                resendTimes += 1
                startLimitAgain()
            } else {
                errorR.invoke()
            }
        }
    }

    private fun startLimitAgain() {
        cancelRunOnUiThread(timeUpdateRunnable)
        updateResend(false)
        timeLimit = TIME_LIMIT * resendTimes
        startTimeUpdate()
    }

    private fun updateResend(resend: Boolean) {
        requireBinding().apply {
            resendButton.visibleOrGone(resend, 1)
            timeLimit.visibleOrGone(!resend, 1)
        }
    }

    private fun startTimeUpdate() {
        if (timeLimit == 0) {
            cancelTimeUpdate()
            if (resendEnabled) {
                updateResend(true)
            }
        } else {
            runOnUiThread(1000, timeUpdateRunnable)
        }
    }

    private fun cancelTimeUpdate() {
        cancelRunOnUiThread(timeUpdateRunnable)
        timeLimit = 0
    }

    override fun onResume() {
        super.onResume()
        showKeyboard(requireBinding().passwordView, true)
    }

    private fun verifyCode(password: String) {
        showProgressBar(true)
        lifecycleScope.launch {
            AuthServerController.getInstance().checkCode(phone,password) {
                showProgressBar(false)
                if (it) {
                    if (register) {
                        openFragment(EnterDetailsFragment())
                    } else {
                        toast("Login success")
                    }
                } else {
                    requireBinding().passwordView.error = "Passcode is incorrect"
                }
            }
        }
    }

    override fun onViewCreated(binding: FragmentVerifyPassowrdBinding) {
        super.onViewCreated(binding)
        actionBarTitle = "Enter a code"

        updateResend(false)
        updateTimeLimit()
        startTimeUpdate()

        binding.apply {
            resendButton.setOnClickListener {
                resendTheCode()
            }
            passwordView.apply {
                doOnTextChanged { text, start, before, count ->
                    text?.let {
                        val password = it.toString()
                        val valid = it.length == 5
                        button.setStateIsActive(valid)
                        if (valid) {

                            verifyCode(password)
                        }
                    }
                }
            }

            phoneNumberText.text = phone
            button.setStateIsActive(false)

            button.setOnClickListener {
                val p = passwordView.text?.toString() ?: return@setOnClickListener
                if (p.isEmpty() || p.length  != 5) {
                    toast("Please enter valid code")
                    return@setOnClickListener
                }
                verifyCode(p)
            }
        }
    }


}