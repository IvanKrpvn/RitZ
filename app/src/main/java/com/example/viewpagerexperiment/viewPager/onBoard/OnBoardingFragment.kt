package com.example.viewpagerexperiment.viewPager.onBoard

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnAttach
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.example.utils.findActivity
import com.example.utils.runOnUiThread
import com.example.utils.toDp
import com.example.utils.toast
import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.databinding.FragmentOnBoardingBinding
import com.example.viewpagerexperiment.ui.actionBar.BaseFragment
import com.example.viewpagerexperiment.viewPager.onBoard.fragments.createAnOrder.CreateAnOrderFragment
import com.example.viewpagerexperiment.viewPager.onBoard.fragments.delegateAnOrder.DelegateAnOrderFragment
import com.example.viewpagerexperiment.viewPager.onBoard.fragments.signUp.SignUpInTheAppFragment
import com.example.viewpagerexperiment.viewPager.onBoard.fragments.trackDriverMovement.TrackDriverMovementFragment
import com.example.viewpagerexperiment.viewPager.onBoard.fragments.yourShipmentIsInsured.YourShipmentIsInsured
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>(R.layout.fragment_on_boarding) {

    override fun onPause() {
        super.onPause()
        //Remove
        findActivity(requireContext())?.apply {
            onBackPressed = null
        }
    }

    override fun onResume() {
        super.onResume()
        //To make onBind work ;))
        onBoardingAdapter.notifyDataSetChanged()

        findActivity(requireContext())?.title = ""
    }

    val fragments = ArrayList<Fragment>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fragments.add(SignUpInTheAppFragment())
        fragments.add(CreateAnOrderFragment())
        fragments.add(DelegateAnOrderFragment())
        fragments.add(YourShipmentIsInsured())
        fragments.add(TrackDriverMovementFragment())

        onBoardingAdapter = OnBoardingAdapter(childFragmentManager, fragments, lifecycle)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = onBoardingAdapter

        viewPager.setPageTransformer(ZoomOutPageTransformer())

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.isSelected) {
                    val pos = tab.position
                    update(pos)
                }
                val show = tab.position != 0
                findActivity(requireContext())?.apply {
                    setBackButtonVis(show)
                    if (tab.position != 0) {
                        onBackPressed = Runnable {
                            viewPager.currentItem = viewPager.currentItem - 1
                        }
                    } else {
                        onBackPressed = null
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

        }.attach()
    }

    private var lastPost = -1

    fun update(position: Int) {
        if (position != lastPost) {
            val fragment = fragments[position]
            lifecycleScope.launch {
                fragment.whenStarted {
                    fragment.view?.let {
                        if (it is ConstraintLayout) {
                            animateBoarding(it)
                        }
                    }
                }
            }
            lastPost = position
        }
    }

    private fun refreshSecondFragmentText() {
        Handler(Looper.getMainLooper()).postDelayed({
            onBoardingAdapter.refreshFragment(1, CreateAnOrderFragment())
        }, 5000)
    }

    inner class OnBoardingAdapter(
        fragmentManager: FragmentManager,
        var fragments: MutableList<Fragment>,
        private val lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun onBindViewHolder(
            holder: FragmentViewHolder,
            position: Int,
            payloads: MutableList<Any>
        ) {
            super.onBindViewHolder(holder, position, payloads)
            //->>>>>>>>>>>


            //Ok fragment view not created yet here ;)))
        }


        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            //Lets try another way
            val f = fragments[position]

            return when (position) {
                0 -> {
                    fragments[0]
                }
                1 -> {

                    fragments[1]
                }
                2 -> {

                    fragments[2]
                }
                3 -> {

                    fragments[3]
                }
                else -> fragments[4]
            }

        }

        fun add(index: Int, fragment: Fragment) {
            fragments.add(index, fragment)
            notifyItemChanged(index)
        }

        fun refreshFragment(index: Int, fragment: Fragment) {
            fragments[index] = fragment
            notifyItemChanged(index)
        }

        fun remove(index: Int) {

            fragments.removeAt(index)
            notifyItemChanged(index)
        }

        override fun getItemId(position: Int): Long {
            return fragments[position].hashCode().toLong()
        }

        override fun containsItem(itemId: Long): Boolean {
            return fragments.find { it.hashCode().toLong() == itemId } != null
        }
    }

    companion object {
        lateinit var viewPager: ViewPager2
        lateinit var onBoardingAdapter: OnBoardingAdapter

        private val mTranslationY = toDp(150)
        private const val mAlpha = 0f
        private val interpolator = DecelerateInterpolator(2f)
        const val duration = 100L

        fun setStartAnim(group: ViewGroup) {
            group.forEach {
                it.apply {
                    animate().cancel()
                    translationY = mTranslationY.toFloat()
                    alpha = mAlpha
                }
            }
        }

        //Every view animated delayed , lets add a delay
        fun animateBoarding(group: ViewGroup) {
            group.forEach {
                it.apply {
                    animate().cancel()
                    translationY = mTranslationY.toFloat()
                    alpha = mAlpha
                }
            }
            var dur = duration
            for (i in 0 until group.childCount) {
                val child = group[i]
                runOnUiThread(Runnable {
                    child.apply {
                        translationY = mTranslationY.toFloat()
                        alpha = mAlpha
                        animate().alpha(1f).translationY(0f)
                            .setInterpolator(interpolator)
                            .setDuration(600).start()
                    }
                }, if (i == 0) 0 else dur.toInt())
                dur += dur / 4
            }
        }
    }

}
