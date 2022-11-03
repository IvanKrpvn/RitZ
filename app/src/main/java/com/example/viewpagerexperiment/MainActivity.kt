package com.example.viewpagerexperiment

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.utils.*
import com.example.viewpagerexperiment.data.UserConfig
import com.example.viewpagerexperiment.data.userLogged
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment
import com.example.viewpagerexperiment.ui.fragments.AuthByPhoneFragment
import com.example.viewpagerexperiment.ui.fragments.UserInfoFragment
import com.example.viewpagerexperiment.viewPager.onBoard.OnBoardingFragment
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    lateinit var container: FrameLayout

    private var currentAlertDialog: AlertDialog? = null

    /**
     *Handles alert dialogs
     */
    fun showAlertDialog(
        title: String,
        message: String,
        positiveText: String,
        positiveClick: () -> Unit,
        negativeText: String,
        negativeClick: () -> Unit,
        onDismissed: (() -> Unit)? = null
    ) {
        cancelAlertDialog()

        currentAlertDialog = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(
                positiveText
            ) { _, _ -> positiveClick.invoke() }
            setNegativeButton(negativeText) { _, _ -> negativeClick.invoke() }
            setOnDismissListener { onDismissed?.invoke() }
        }.create().also {
            currentAlertDialog = it
        }
    }

    fun cancelAlertDialog() {
        currentAlertDialog?.apply {
            dismiss()
            currentAlertDialog = null
        }
    }

    fun newId() = System.currentTimeMillis().toString()

    /**
     * UseFull when navigating between fragments(BaseFragment)
     */
    private var stacks = arrayListOf<String>()

    private fun lastFragmentId() = stacks.getOrNull(stacks.lastIndex - 1) ?: ""
    private fun currentFragmentId() = stacks.lastOrNull() ?: ""

    private fun getFragment(tag: String) = supportFragmentManager.findFragmentByTag(tag)

    private fun FragmentTransaction.removeFragment(fragment: Fragment) {
        remove(fragment)
    }

    //Opens new fragment or shows it if already exists
    fun openFragment(fragment: BaseFragment<*>, removeLast: Boolean, animate: Boolean = true) {
        val currentFragment = getFragment(currentFragmentId())
        var id = fragment.tag
        if (id == null) {
            id = newId()
        }
        supportFragmentManager.commit {
            if (animate) {
                setCustomAnimations(
                    R.anim.fragment_open_anim,
                    R.anim.fragment_close_anim,
                    R.anim.fragment_pop_enter,
                    R.anim.fragment_pop_exit
                )
            }
            if (currentFragment != null) {
                if (removeLast) {
                    removeFragment(currentFragment)
                } else {
                    hide(currentFragment)
                }
            }
            if (supportFragmentManager.findFragmentByTag(id) != null) {
                show(fragment)
                fragment.onResume()
            } else {
                add(R.id.container, fragment, id)
            }
        }
        currentFragment?.onPause()

        stacks.remove(id)
        stacks.add(id)

        if (removeLast) {
            if (currentFragment != null) {
                stacks.remove(currentFragment.tag)
            }
        }
    }

    //Closes previous fragment if exists
    fun closePreviousFragment() {
        val s = stacks
        supportFragmentManager.apply {
            if (s.size > 1) {
                val previous = supportFragmentManager.findFragmentByTag(lastFragmentId())
                if (previous != null)
                    commit {
                        removeFragment(previous)
                        (previous as BaseFragment<*>)
                        s.remove(previous.tag)
                    }
            }
        }
    }

    //Closes current fragment
    fun closeLastFragment() {
        supportFragmentManager.apply {
            if (fragments.size == 1) return

            val current = findFragmentByTag(currentFragmentId())
            val previous = findFragmentByTag(lastFragmentId())

            commit {
                setCustomAnimations(
                    R.anim.fragment_pop_enter,
                    R.anim.fragment_pop_exit,
                    R.anim.fragment_pop_enter,
                    R.anim.fragment_pop_exit
                )

                if (current != null) {
                    removeFragment(current)
                }
                if (previous != null) {
                    show(previous)
                }
                if (current is BaseFragment<*>) {
                    this@MainActivity.stacks.remove(current.tag)
                }
            }
            previous?.onResume()
        }
    }

    var onBackPressed: Runnable? = null

    //Handles back presses
    override fun onBackPressed() {
        if (onBackPressed != null) {
            onBackPressed?.run()
        } else {
            if (stacks.size == 1) {
                finish()
            } else {
                closeLastFragment()
            }
        }
    }

    private lateinit var toolBar: MaterialToolbar

    fun showActionBar(show: Boolean) = requireActionBar().apply {
        if (show) {
            show()
        } else {
            hide()
        }
    }

    fun requireActionBar() = supportActionBar!!

    private lateinit var progressBar: ViewGroup

    fun showProgressBar(show: Boolean) {
        progressBar.visibleOrGone(show, 1)
        if (show) {
            currentFocus?.let {
                hideKeyboard(this)
            }
        }
    }

    var currentFocusEditView: View? = null

    private var backButtonVisible = false

    @RequiresApi(Build.VERSION_CODES.O)
    fun setBackButtonVis(vis: Boolean) {
        if (backButtonVisible == vis) return
        backButtonVisible = vis
        requireActionBar().apply {
            setDisplayHomeAsUpEnabled(vis)
            setDisplayShowHomeEnabled(vis)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = LayoutInflater.from(this)

        setContentView(R.layout.activity_main)
        container = findViewById(R.id.container)
        toolBar = findViewById(R.id.tool_bar)
        progressBar = findViewById(R.id.progress_container)
        progressBar.apply {
            visibleOrGone(false)
            setOnClickListener {
                //Just overridden to not make clickable
            }
        }

        setSupportActionBar(toolBar)

        requireActionBar().apply {
            setDisplayHomeAsUpEnabled(true)
            title = null
        }

        toolBar.isTitleCentered = true
        toolBar.setTitleTextColor(darkGrey)

        UserConfig
        if (UserConfig.firstTime) {
            openFragment(OnBoardingFragment(), true)
        } else {
            if (userLogged()) {
                openFragment(UserInfoFragment(),false)
            } else {
                openFragment(AuthByPhoneFragment(),false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}



