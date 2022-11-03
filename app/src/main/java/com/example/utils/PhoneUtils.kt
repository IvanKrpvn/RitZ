package com.example.utils

import android.widget.EditText
import com.github.vacxe.phonemask.PhoneMaskManager

fun bindPhoneValidator(editText: EditText) : PhoneMaskManager {
    return PhoneMaskManager()
        .withRegion("+")
        .bindTo(editText)
}

fun checkPhoneIsValid(phone: String,t: Boolean = false): Boolean {
    val valid = phone.length >= 10
    if (!valid && t) {
        toast("Enter a valid phone number")
    }
    return valid
}

