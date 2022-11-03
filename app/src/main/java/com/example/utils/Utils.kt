package com.example.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Vibrator
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.viewpagerexperiment.App
import com.example.viewpagerexperiment.MainActivity
import com.example.viewpagerexperiment.appContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

fun handler() = App.handler

//Float to dp
fun toDp(value: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        appContext.resources.displayMetrics
    ).toInt()
}

//Float to dp
fun toDp(value: Int): Int {
    return toDp(value.toFloat())
}

//Shows keyboard
fun showKeyboard(editText: View?, show: Boolean) {
    if (editText == null) return
    val context = editText.context
    findActivity(context)?.currentFocusEditView = editText
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.apply {
        if (show) {
            editText.requestFocus()
            showSoftInput(editText, InputMethodManager.SHOW_FORCED)
        } else {
            if (isActive) {
                toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
            }
        }
    }
}

//Shows keyboard
fun EditText.showKeyboard(show: Boolean = true) {
    showKeyboard(this, show)
}

/**
 * Extension for coroutine
 */
@OptIn(DelicateCoroutinesApi::class)
fun launchOnBack(runnable: () -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        runnable.invoke()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun launchOnMain(runnable: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        runnable.invoke()
    }
}

//To avoid context wrapper
fun findActivity(context: Context): MainActivity? {
    if (context is Activity) return context as MainActivity
    if (context is ContextWrapper) return findActivity(context.baseContext)
    return null
}

fun hideKeyboard(activity: Activity) {
    try {
        var view = activity.currentFocus
        if (view == null) {
            view = findActivity(activity)?.currentFocusEditView
        }
        view?.apply {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    } catch (e: java.lang.Exception) {
        log(e)
    }
}


/**
 * Run on ui thread - best for running and canceling runnable
 */
fun runOnUiThread(runnable: Runnable, delay: Int = 0) {
    handler().apply {
        if (delay != 0) {
            postDelayed(runnable, delay.toLong())
        } else {
            post(runnable)
        }
    }
}

fun runOnUiThread(delay: Int, unit: () -> Unit) {
    runOnUiThread(unit, delay)
}

fun cancelRunOnUiThread(runnable: Runnable?) {
    if (runnable == null) return
    handler().removeCallbacks(runnable)
}

/**
 * Vibration
 */
fun vibrate(delay: Long) {
    val manager =
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    manager.vibrate(delay, null)
}

/**
 * Toast
 */
fun toast(message: String?) {
    if (message == null) return
    Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
}

fun randomId() = UUID.randomUUID().toString()
fun currentDate() = System.currentTimeMillis()

//We can change everything ;)) private fields
fun setValue(obj: Any, fieldName: String, value: Any?) {
    try {
        val field = obj.javaClass.getDeclaredField(fieldName)
        val acs = field.isAccessible
        field.isAccessible = true
        field.set(obj, value)
        field.isAccessible = acs
    } catch (e: Exception) {

    }
}

fun TextView.setLinkedText(text: String, vararg urlTextAndUrl: Pair<String, String>) {
    setHintTextColor(red)
    movementMethod = LinkMovementMethod.getInstance()
    val spannableString = SpannableString(text)
    urlTextAndUrl.forEach {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebUrl(findActivity(context)!!, it.second)
            }
        }
        val index = text.indexOf(it.first)
        val end = index + it.first.length
        spannableString.setSpan(clickableSpan, index, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    setText(spannableString)
}

fun openWebUrl(activity: Activity, url: String) {
    if (url.isEmpty()) return
    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun sharedConfig(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences =
    appContext.getSharedPreferences(name, mode)