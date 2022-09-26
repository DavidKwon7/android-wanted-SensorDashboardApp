package com.preonboarding.sensordashboard.presentation.replay

import android.os.Bundle
import android.view.View
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentReplayBinding
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp

class ReplayFragment : BaseFragment<FragmentReplayBinding>(R.layout.fragment_replay) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.tbReplay.setNavigationOnClickListener {
            navigateUp()
        }
    }
}