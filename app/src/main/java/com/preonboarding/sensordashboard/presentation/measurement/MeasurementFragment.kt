package com.preonboarding.sensordashboard.presentation.measurement

import android.os.Bundle
import android.view.View
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentMeasurementBinding
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp

class MeasurementFragment : BaseFragment<FragmentMeasurementBinding>(R.layout.fragment_measurement) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.tbMeasurement.setNavigationOnClickListener {
            navigateUp()
        }
    }
}