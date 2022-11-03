package com.example.utils

import android.animation.Animator
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.appContext
import com.github.vacxe.phonemask.PhoneMaskManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

//Utils for binding
lateinit var inflater: LayoutInflater

fun <T : ViewDataBinding> inflateBinding(
    parent: ViewGroup?,
    layId: Int,
    attach: Boolean = false
): T {
    try {
        inflater.hashCode()
    } catch (e: Exception) {

    }
    return DataBindingUtil.inflate(inflater, layId, parent, attach)
}

/**
 * Custom done for this project
 *Anim types:
 * 0 - Fade
 * 1 - Scale
 */
@BindingAdapter("visibleOrGone", "type", "onAnimEnd", "duration", "invisible", requireAll = false)
fun View.visibleOrGone(
    visibleOrGone: Boolean,
    type: Int = -1,
    onAnimEnd: (() -> Unit)? = null,
    duration: Long = 300,
    invisible: Boolean = false
) {
    if (visibleOrGone == isVisible) return
    if (type != -1) {
        visibility = View.VISIBLE
        if (visibleOrGone) {
            alpha = 0f
            if (type == 1) {
                scaleY = 0.8f
                scaleX = 0.8f
            }
        }
        var anim = animate().alpha(if (visibleOrGone) 1f else 0f).setDuration(duration)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!visibleOrGone) {
                        visibility = if (invisible) View.INVISIBLE else View.GONE
                    } else {
                        alpha = 1f
                        scaleY = 1f
                        scaleX = 1f
                    }
                    onAnimEnd?.invoke()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        if (type == 1) {
            val scale = if (visibleOrGone) 1f else 0.8f
            anim = anim.scaleX(scale).scaleY(scale).setInterpolator(OvershootInterpolator(2.5f))
        }
        anim.start()

    } else {
        visibility =
            if (visibleOrGone) View.VISIBLE else if (invisible) View.INVISIBLE else View.GONE
        onAnimEnd?.invoke()
    }
}

fun TextInputLayout.clearError() {
    isErrorEnabled = false
    error = ""
}

/**
 * Error for editText
 */
fun TextInputLayout.applyError(error: String, removeWhenChanged: Boolean = true) {
    if (error == this.error) return
    if (error.isEmpty()) {
        isErrorEnabled = false
    }
    if (removeWhenChanged) {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runOnUiThread(50) {
                    editText?.removeTextChangedListener(this)
                }
                clearError()
            }
        }
        editText!!.addTextChangedListener(watcher)
    }
    isErrorEnabled = true
    setError(error)
}

val red = ResourcesCompat.getColor(appContext.resources, R.color.red, null)
val white = ResourcesCompat.getColor(appContext.resources, R.color.white, null)
val grey = ResourcesCompat.getColor(appContext.resources, R.color.grey, null)
val darkGrey = ResourcesCompat.getColor(appContext.resources, R.color.dark_grey, null)
val redTransparent =
    ResourcesCompat.getColor(appContext.resources, R.color.tab_color_transparent, null)
val tabBack = ResourcesCompat.getColor(appContext.resources, R.color.tabBack, null)

/**
 * Set button state change
 */
fun MaterialButton.setStateIsActive(active: Boolean) {
    if (active) {
        backgroundTintList =
            ColorStateList.valueOf(red)
        setTextColor(white)
        strokeWidth = 0
    } else {
        backgroundTintList = ColorStateList.valueOf(white)
        strokeWidth = toDp(1)
        setTextColor(red)
        strokeColor = ColorStateList.valueOf(red)
    }
    compoundDrawablesRelative.forEach {
        if (it == null) return
        it.setTint(if (active) white else red)
    }
    isActivated = active
}

private var textColorHintList =
    ResourcesCompat.getColorStateList(appContext.resources, R.color.text_color_hint, null)

/**
 * Set text input layout hint color
 */
fun TextInputLayout.applyTextHintColor() {
    setValue(this, "focusedTextColor", textColorHintList)
}

val mask: PhoneMaskManager = PhoneMaskManager()
    .withMask("#-###-###-########")
    .withRegion("+ ")

fun TextInputLayout.applyPhoneEditWatcher() {
    mask.bindTo(editText!!)
    editText!!.append("1 ")
    return
}

fun TextInputLayout.checkEmpty(): Boolean {
    val empty = editText!!.text.toString().isEmpty()
    if (empty) {
        applyError("This field is required")
    }
    return empty
}

fun TextInputLayout.latinNotAllowed() {
    var canEdit = true
    editText!!.addTextChangedListener {
        if (!canEdit) return@addTextChangedListener
        clearError()
        it?.apply {
            canEdit = false
            val text = toString()
            val builder = StringBuilder()
            text.forEach { c ->
                val checkLatin = checkIsTextLatin(c.toString())
                if (!checkLatin) {
                    applyError("Latin not allowed", canEdit)
                } else {
                    builder.append(c)
                }
            }
            clear()
            append(builder.toString())
        }
        canEdit = true
    }
}
