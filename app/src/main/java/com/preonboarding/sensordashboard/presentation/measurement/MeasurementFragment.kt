package com.preonboarding.sensordashboard.presentation.measurement

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.snackbar.Snackbar
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentMeasurementBinding
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import com.preonboarding.sensordashboard.domain.model.MeasureTarget
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.state.MeasurementUiState
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeasurementFragment : BaseFragment<FragmentMeasurementBinding>(R.layout.fragment_measurement),
    SensorEventListener {
    private val viewModel: MeasurementViewModel by viewModels()

    // sensor
    private val sensorManager: SensorManager by lazy {
        requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val accSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private val gyroSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    // graph
    var sensorInfoList: ArrayList<SensorInfo> = arrayListOf()

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        bindingViewModel()
    }

    private fun bindingViewModel() {
        binding.fragment = this@MeasurementFragment
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

    fun startMeasurement() {
        if (viewModel.curSecond.value < MAX_SECOND) {

            viewModel.setIsMeasuring(true) // 측정 시작

            when (viewModel.curMeasureTarget.value) {
                MeasureTarget.ACC -> {
                    sensorManager.registerListener(
                        this,
                        accSensor,
                        SENSOR_DELAY_CUSTOM
                    )
                }

                MeasureTarget.GYRO -> {
                    sensorManager.registerListener(
                        this,
                        gyroSensor,
                        SENSOR_DELAY_CUSTOM
                    )
                }
            }
        }
        else {
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                getString(R.string.measure_snack_bar_second_text),
                Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    fun stopMeasurement() {
        sensorManager.unregisterListener(this)
        viewModel.setIsMeasuring(false)
    }

    fun saveMeasurement() {
        with(viewModel) {
            if (isMeasuring.value) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.measure_snack_bar_measuring_text),
                    Snackbar.LENGTH_SHORT)
                    .show()
            }
            else {
                if (sensorList.value.isEmpty()) {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.measure_snack_bar_empty_text),
                        Snackbar.LENGTH_SHORT)
                        .show()
                }
                else {
                    this.saveMeasurement()
                    checkUiState()
                }
            }
        }
    }

    fun changeMeasureTarget() {
        stopMeasurement() // 센서 측정 중지

        // 그래프 초기화
        clearChart()

        // 센서 값 초기화
        with(viewModel) {
            clearMeasurementInfo() // 측정 타겟 바뀌면 센서 값 리스트 & time 초기화
            when (curMeasureTarget.value) {
                MeasureTarget.ACC -> {
                    setMeasureTarget(MeasureTarget.GYRO)
                }
                MeasureTarget.GYRO -> {
                    setMeasureTarget(MeasureTarget.ACC)
                }
            }
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        val sensorInfo = when (sensorEvent?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE  -> {
                SensorInfo(
                    x = sensorEvent.values[0].toInt(),
                    y = sensorEvent.values[1].toInt(),
                    z = sensorEvent.values[2].toInt(),
                )
            }

            else -> {
                SensorInfo.emptyInfo()
            }
        }

        // update second & value
        viewModel.updateMeasurement(sensorInfo)

        // chart update
        sensorInfoList.add(sensorInfo)
        updateChart()

        lifecycleScope.launch {
                viewModel.curSecond.collect {
                    if(it >= MAX_SECOND) {
                        // 60초 지나면 측정 중지
                        stopMeasurement()
                        this@launch.cancel()
                    }
                }
        }
    }

    private fun clearChart() {
        binding.measurementLineChart.clear()
        sensorInfoList.clear()
    }

    private fun updateChart() {
        val entriesX = ArrayList<Entry>()
        val entriesY = ArrayList<Entry>()
        val entriesZ = ArrayList<Entry>()

        var i = 1F

        for (it in sensorInfoList) {
            entriesX.add(Entry(i, it.x.toFloat()))
            entriesY.add(Entry(i, it.y.toFloat()))
            entriesZ.add(Entry(i, it.z.toFloat()))
            i++
        }

        val dataSetX = LineDataSet(entriesX, "X")
        val dataSetY = LineDataSet(entriesY, "Y")
        val dataSetZ = LineDataSet(entriesZ, "Z")

        dataSetX.color = Color.RED
        dataSetX.setDrawCircles(false)
        dataSetX.setDrawValues(false)

        dataSetY.color = Color.GREEN
        dataSetY.setDrawValues(false)
        dataSetY.setDrawCircles(false)

        dataSetZ.color = Color.BLUE
        dataSetZ.setDrawValues(false)
        dataSetZ.setDrawCircles(false)

        val lineData = LineData()

        lineData.addDataSet(dataSetX)
        lineData.addDataSet(dataSetY)
        lineData.addDataSet(dataSetZ)

        binding.measurementLineChart.apply {
            data = lineData

            lineData.notifyDataChanged()
            notifyDataSetChanged()
            invalidate()
        }
    }

    private fun checkUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect {
                    when(it) {
                        is MeasurementUiState.Loading -> {}
                        is MeasurementUiState.SaveSuccess -> {
                            clearChart()
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                getString(R.string.measure_snack_bar_save_text),
                                Snackbar.LENGTH_SHORT)
                                .show()
                        }

                        is MeasurementUiState.SaveFail -> {
                            Snackbar.make(
                                requireActivity().findViewById(android.R.id.content),
                                getString(R.string.measure_snack_bar_not_save_text),
                                Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        /** Not Use **/
    }

    companion object {
        private const val TAG = "MeasurementFragment"
        private const val PERIOD = 100L // 10Hz -> 1초에 10번 -> 0.1초 주기로 받아옴
        private const val SENSOR_DELAY_CUSTOM = 100000
        private const val MAX_SECOND = 60.0 // 60초
    }

}