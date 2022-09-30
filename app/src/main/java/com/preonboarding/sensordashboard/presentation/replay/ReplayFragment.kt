package com.preonboarding.sensordashboard.presentation.replay

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentReplayBinding
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReplayFragment : BaseFragment<FragmentReplayBinding>(R.layout.fragment_replay) {

    private val viewModel: ReplayViewModel by activityViewModels()
    private val args: ReplayFragmentArgs by navArgs()

    /**
     * @author 이재성
     * args로 ViewType, MeasurementResult 사용하시면 됩니다!!
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()
        initViews()
        var viewType = args.viewType
        viewModel.setReplayViewType(viewType)

        Timber.e("${args.viewType}")
        Timber.e("${args.measureResult}")
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