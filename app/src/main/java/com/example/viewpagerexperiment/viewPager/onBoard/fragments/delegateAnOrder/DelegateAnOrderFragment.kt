package com.example.viewpagerexperiment.viewPager.onBoard.fragments.delegateAnOrder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.viewpagerexperiment.R
import com.example.viewpagerexperiment.databinding.FragmentDelegateAnOrderBinding
import com.example.viewpagerexperiment.viewPager.onBoard.OnBoardingFragment


class DelegateAnOrderFragment : Fragment() {
lateinit var binding:FragmentDelegateAnOrderBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentDelegateAnOrderBinding.inflate(inflater,container,false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonNext.setOnClickListener {

            OnBoardingFragment.viewPager.setCurrentItem(OnBoardingFragment.viewPager.currentItem+1,true)

        }
    }

}