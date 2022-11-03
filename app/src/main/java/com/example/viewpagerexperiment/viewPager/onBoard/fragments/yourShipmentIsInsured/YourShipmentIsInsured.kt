package com.example.viewpagerexperiment.viewPager.onBoard.fragments.yourShipmentIsInsured

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewpagerexperiment.databinding.FragmentYourShipmentIsInsuredBinding
import com.example.viewpagerexperiment.viewPager.onBoard.OnBoardingFragment


class YourShipmentIsInsured : Fragment() {
    lateinit var binding: FragmentYourShipmentIsInsuredBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYourShipmentIsInsuredBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonNext.setOnClickListener {

            OnBoardingFragment.viewPager.setCurrentItem(
                OnBoardingFragment.viewPager.currentItem + 1,
                true
            )

        }
    }


}