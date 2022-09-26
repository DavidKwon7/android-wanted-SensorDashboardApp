package com.preonboarding.sensordashboard.presentation.dashboard

import android.os.Bundle
import android.view.View
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentDashboardBinding
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigate

class DashboardFragment : BaseFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(binding) {
        tvMeasurement.setOnClickListener {
            navigate(R.id.action_dashboard_to_measurement)
        }

        tvReplay.setOnClickListener {
            navigate(R.id.action_dashboard_to_replay)
        }
    }
}