package com.preonboarding.sensordashboard.presentation.measurement

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentMeasurementBinding
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.preonboarding.sensordashboard.domain.model.GyroInfo
import com.preonboarding.sensordashboard.domain.model.MeasureTarget
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MeasurementFragment : BaseFragment<FragmentMeasurementBinding>(R.layout.fragment_measurement), SensorEventListener {
    private val viewModel: MeasurementViewModel by viewModels()

    private val sensorManager: SensorManager by lazy {
        requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val accSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private val gyroSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

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
            saveMeasurement()
        }

        // ACC 선택
        binding.tvMeasureAcc.setOnClickListener {
            changeMeasureTarget()
        }

        // GYRO 선택
        binding.tvMeasureGyro.setOnClickListener {
            changeMeasureTarget()
        }

        binding.apply {
            val thread = ThreadClass()
            thread.start()
        }
    }

    private fun startMeasurement() {
        Timber.tag(TAG).e("START")

        when(viewModel.curMeasureTarget.value) {
                MeasureTarget.ACC -> {
                    sensorManager.registerListener(
                        this,
                        accSensor,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )

                }

                MeasureTarget.GYRO -> {
                    sensorManager.registerListener(
                        this,
                        gyroSensor,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
        }
    }

    private fun stopMeasurement() {
        Timber.tag(TAG).e("STOP")
        sensorManager.unregisterListener(this)
    }

    private fun saveMeasurement() {
        with(viewModel) {
            if(accList.value.isEmpty() || gyroList.value.isEmpty()) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.measure_snack_bar_text),
                    Snackbar.LENGTH_SHORT)
                    .show()
            }
            else {
                saveMeasurement()
            }
        }
    }

    private fun changeMeasureTarget() {

        with(viewModel) {
            when(curMeasureTarget.value) {
                MeasureTarget.ACC -> {
                    setMeasureTarget(MeasureTarget.GYRO)
                }
                MeasureTarget.GYRO -> {
                    setMeasureTarget(MeasureTarget.ACC)

                }
            }
            Timber.tag(TAG).e("현재 측정 타겟 : ${curMeasureTarget.value}")
        }
        stopMeasurement()
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        when(sensorEvent?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val accInfo = AccInfo(
                    x = sensorEvent.values[0].toInt(),
                    y = sensorEvent.values[1].toInt(),
                    z = sensorEvent.values[2].toInt(),
                )
                viewModel.accList.value.add(accInfo)
                Timber.tag(TAG).d("acc : $accInfo")
            }

            Sensor.TYPE_GYROSCOPE -> {
                val gyroInfo = GyroInfo(
                    x = (sensorEvent.values[0] * THOUS).toInt(),
                    y = (sensorEvent.values[1] * THOUS).toInt(),
                    z = (sensorEvent.values[2] * THOUS).toInt(),
                )
                viewModel.gyroList.value.add(gyroInfo)
                Timber.tag(TAG).d("gyro : $gyroInfo")
            }
        }

    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        /** Not Use **/
    }

    companion object {
        private const val TAG = "MeasurementFragment"
        private const val THOUS = 1000
        private const val PERIOD = 100 // 10Hz -> 0.1초 주기로 받아옴
        private const val MAX = 60000 // 60초
    }

    inner class ThreadClass : Thread() {
        override fun run() {
            val input = Array<Double>(100,{Math.random()})
            // Entry 배열 생성
            var entries: ArrayList<Entry> = ArrayList()
            // Entry 배열 초기값 입력
            entries.add(Entry(0F , 0F))
            // 그래프 구현을 위한 LineDataSet 생성
            var dataset: LineDataSet = LineDataSet(entries, "input")
            // 그래프 data 생성 -> 최종 입력 데이터
            var data: LineData = LineData(dataset)
            // chart.xml에 배치된 lineChart에 데이터 연결
            binding.measurementLineChart.data = data

            requireActivity().runOnUiThread {
                binding.measurementLineChart.animateXY(1, 1)
            }

            for (i in 0 until input.size){

                SystemClock.sleep(10)
                data.addEntry(Entry(i.toFloat(), input[i].toFloat()), 0)
                data.notifyDataChanged()
                binding.measurementLineChart.notifyDataSetChanged()
                binding.measurementLineChart.invalidate()
            }

        }
    }
}