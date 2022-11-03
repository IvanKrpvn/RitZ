package com.example.viewpagerexperiment.viewPager.onBoard.fragments.createAnOrder

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewpagerexperiment.databinding.FragmentCreateAnOrderBinding
import com.example.viewpagerexperiment.viewPager.onBoard.OnBoardingFragment

class CreateAnOrderFragment : Fragment() {

    lateinit var binding: FragmentCreateAnOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAnOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNext.setOnClickListener {
            OnBoardingFragment.viewPager.adapter?.notifyDataSetChanged()
            OnBoardingFragment.viewPager.setCurrentItem(
                OnBoardingFragment.viewPager.currentItem + 1,
                true
            )
        }
    }


}