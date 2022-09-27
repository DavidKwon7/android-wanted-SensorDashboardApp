package com.preonboarding.sensordashboard.presentation.measurement

import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentMeasurementBinding
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.preonboarding.sensordashboard.domain.model.GyroInfo
import com.preonboarding.sensordashboard.domain.model.MeasureTarget
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp
import timber.log.Timber

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

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(
            this,
            accSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        sensorManager.registerListener(
            this,
            gyroSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
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
    }

    private fun startMeasurement() {
        when(viewModel.curMeasureTarget.value) {

                MeasureTarget.ACC -> {

                }

                MeasureTarget.GYRO -> {

                }
        }
    }

    private fun stopMeasurement() {

    }

    private fun saveMeasurement() {

    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {

        when(sensorEvent?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val accInfo = AccInfo(
                    x = sensorEvent.values[0].toInt(),
                    y = sensorEvent.values[1].toInt(),
                    z = sensorEvent.values[2].toInt(),
                )
                Timber.tag(TAG).d("acc : $accInfo")
            }

            Sensor.TYPE_GYROSCOPE -> {
                val gyroInfo = GyroInfo(
                    x = (sensorEvent.values[0] * THOUS).toInt(),
                    y = (sensorEvent.values[1] * THOUS).toInt(),
                    z = (sensorEvent.values[2] * THOUS).toInt(),
                )
                Timber.tag(TAG).d("gyro : $gyroInfo")
            }
        }

    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    companion object {
        private const val TAG = "MeasurementFragment"
        private const val THOUS = 1000
    }
}