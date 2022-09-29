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
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MeasurementFragment : BaseFragment<FragmentMeasurementBinding>(R.layout.fragment_measurement),
    SensorEventListener {
    private val viewModel: MeasurementViewModel by viewModels()

    // private val channel = Channel<SensorInfo>()

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

        // 측정을 중지하거나 60초가 넘는다고 채널이 중단 되는 것 아님
        // 채널이 중단 되는 경우 -> 채널에 더 이상 아무런 데이터를 보내거나 받지 않을 경우
        // 즉 화면 나갈 때
        // channel.close()
    }

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

        binding.btnMeasureStart.setOnClickListener {
            startMeasurement()
        }

        binding.btnMeasurePause.setOnClickListener {
            stopMeasurement()
        }

        binding.btnMeasureSave.setOnClickListener {
            if (viewModel.isMeasuring.value) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.measure_snack_bar_measuring_text),
                    Snackbar.LENGTH_SHORT)
                    .show()
            }
            else {
                saveMeasurement()
            }
        }

        // ACC 선택
        binding.tvMeasureAcc.setOnClickListener {
            changeMeasureTarget()
        }

        // GYRO 선택
        binding.tvMeasureGyro.setOnClickListener {
            changeMeasureTarget()
        }

    }

    private fun startMeasurement() {
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

    private fun stopMeasurement() {
        sensorManager.unregisterListener(this)
        viewModel.setIsMeasuring(false)
    }

    private fun saveMeasurement() {
        with(viewModel) {
            if (sensorList.value.isEmpty()) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.measure_snack_bar_empty_text),
                    Snackbar.LENGTH_SHORT)
                    .show()
            }
            else {
                saveMeasurement()

                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        saveState.collect {
                            when(it) {
                                true -> {
                                    // 저장 성공
                                    clearChart()
                                    Snackbar.make(
                                        requireActivity().findViewById(android.R.id.content),
                                        getString(R.string.measure_snack_bar_save_text),
                                        Snackbar.LENGTH_SHORT)
                                        .show()
                                }

                                false -> {
                                    // 저장 실패
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
        }
    }

    private fun changeMeasureTarget() {
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
            Timber.tag(TAG).e("현재 측정 타겟 : ${curMeasureTarget.value}")
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
        Timber.tag(TAG).d("${viewModel.curMeasureTarget.value.type} : $sensorInfo")
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

//        lifecycleScope.launch {
//            // 0.1초마다 send
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                delay(PERIOD)
//                channel.send(sensorInfo)
//                viewModel.plusCurSecond()
//
//                viewModel.curSecond.collect {
//                    if(it >= MAX) {
//                        // 60초 지나면 측정 중지
//                        stopMeasurement()
//                        this@launch.cancel()
//                    }
//                }
//            }
//        }
//
//        lifecycleScope.launch {
//            // 0.1초마다 receive
//            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                for (sensor in channel) {
//                    viewModel.sensorList.value.add(sensor)
//                    sensorInfoList.add(sensor)
//                    updateChart()
//                    Timber.tag(TAG).d("${viewModel.curMeasureTarget.value.type} : $sensorInfo")
//                }
//            }
//        }

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