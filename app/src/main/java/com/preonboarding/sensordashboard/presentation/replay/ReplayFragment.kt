package com.preonboarding.sensordashboard.presentation.replay

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentReplayBinding
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReplayFragment : BaseFragment<FragmentReplayBinding>(R.layout.fragment_replay) {

    private val viewModel: ReplayViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()
        initViews()
    }

    private fun registerObserver() {
        binding.viewmodel = viewModel

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.curViewType.collect {
                    binding.viewType = it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.curPlayType.collect {
                    binding.playType = it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.timerCount.collect {
                    binding.timerCount = it
                }
            }
        }
    }

    private fun initViews() {
        binding.tbReplay.setNavigationOnClickListener {
            navigateUp()
        }
    }
}