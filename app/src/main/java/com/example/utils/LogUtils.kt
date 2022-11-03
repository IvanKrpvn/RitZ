package com.example.utils

import android.util.Log
import java.lang.Exception

const val TAG = "MY APP"

fun log(text: String?) {
    if (text == null) return
    Log.i(TAG,text)
}

fun log(exception: Exception?) {
    log(text = exception?.message)
}

