package com.example.viewpagerexperiment.ui.actionBar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.utils.findActivity
import com.example.utils.hideKeyboard
import com.example.utils.inflateBinding
import com.example.viewpagerexperiment.MainActivity

open class BaseFragment<T : ViewDataBinding>(val layId: Int) : Fragment() {

    private var binding: T? = null

    var hasBackButton = true
    var hasActionBar = true

    fun requireBinding() = binding!!

    open fun onViewCreated(binding: T) {}

    fun showAlertDialog(
        title: String,
        message: String,
        positiveText: String,
        positiveClick: () -> Unit,
        negativeText: String,
        negativeClick: () -> Unit,
        onDismissed: (() -> Unit)? = null
    ) {
        findActivity(requireContext())?.showAlertDialog(
            title,
            message,
            positiveText,
            positiveClick,
            negativeText,
            negativeClick,
            onDismissed
        )
    }

    fun getActionBar() = activity().requireActionBar()

    fun activity() = findActivity(requireContext()) as MainActivity

    var actionBarTitle = ""
    set(value) {
        activity().requireActionBar().title = value
        field = value
    }

    fun cancelAlertDialog() {
        activity().cancelAlertDialog()
    }

    fun showProgressBar(show: Boolean) {
        activity().showProgressBar(show)
    }

    override fun onResume() {
        super.onResume()
        activity().showActionBar(hasActionBar)
        activity().setBackButtonVis(hasBackButton)
        activity().requireActionBar().title = actionBarTitle

    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(activity())
    }

    fun openFragment(fragment: BaseFragment<*>,removeLast: Boolean = false) {
        activity().openFragment(fragment,removeLast)
    }

    fun closeLastFragment() {
        activity().closeLastFragment()
    }

    fun closePreviousFragment() {
        activity().closePreviousFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflateBinding<T>(container, layId).apply {
            binding = this
            onViewCreated(this)
        }.root
    }
}