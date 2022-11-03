package com.example.viewpagerexperiment.viewPager.onBoard.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewpagerexperiment.databinding.FragmentSignUpInTheAppBinding
import com.example.viewpagerexperiment.viewPager.onBoard.OnBoardingFragment

class SignUpInTheAppFragment() : Fragment() {

    lateinit var binding: FragmentSignUpInTheAppBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpInTheAppBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            binding.buttonNext.setOnClickListener {
                OnBoardingFragment.viewPager.setCurrentItem(
                    OnBoardingFragment.viewPager.currentItem + 1,
                    true
                )
            }
        }

    }
}