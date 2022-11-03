package com.example.viewpagerexperiment.viewPager.onBoard.fragments.trackDriverMovement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.utils.findActivity
import com.example.viewpagerexperiment.databinding.FragmentTrackDriverMovementBinding
import com.example.viewpagerexperiment.ui.fragments.AuthByPhoneFragment
import com.example.viewpagerexperiment.ui.fragments.AuthSplashChooseFragment
import com.example.viewpagerexperiment.viewPager.onBoard.OnBoardingFragment

class TrackDriverMovementFragment : Fragment() {
    lateinit var binding: FragmentTrackDriverMovementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackDriverMovementBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonNext.setOnClickListener {
            findActivity(requireContext())?.openFragment(AuthByPhoneFragment(), false)
        }
    }

}