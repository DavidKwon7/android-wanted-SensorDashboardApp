package com.preonboarding.sensordashboard.presentation.measurement

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentMeasurementBinding
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp

class MeasurementFragment : BaseFragment<FragmentMeasurementBinding>(R.layout.fragment_measurement) {
    private val viewModel: MeasurementViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        bindingViewModel()
    }

    private fun bindingViewModel() {
        binding.viewModel = viewModel
        binding.measureTarget = viewModel.curMeasureTarget.value

        lifecycleScope.launchWhenCreated {
            viewModel.curMeasureTarget.collect {
                binding.measureTarget = it
            }
        }
    }

    private fun initViews() {
        binding.tbMeasurement.setNavigationOnClickListener {
            navigateUp()
        }
    }
}