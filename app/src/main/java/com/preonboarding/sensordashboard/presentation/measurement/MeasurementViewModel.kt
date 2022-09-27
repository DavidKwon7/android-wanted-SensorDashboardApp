package com.preonboarding.sensordashboard.presentation.measurement

import androidx.lifecycle.ViewModel
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.preonboarding.sensordashboard.domain.model.GyroInfo
import com.preonboarding.sensordashboard.domain.model.MeasureTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class MeasurementViewModel: ViewModel() {

    private val _curMeasureTarget: MutableStateFlow<MeasureTarget> =
        MutableStateFlow(MeasureTarget.ACC)
    val curMeasureTarget: StateFlow<MeasureTarget>
        get() = _curMeasureTarget

    private val _accList: MutableStateFlow<MutableList<AccInfo>> =
        MutableStateFlow(mutableListOf())
    val accList: StateFlow<MutableList<AccInfo>>
        get() = _accList

    private val _gyroList: MutableStateFlow<MutableList<GyroInfo>> =
        MutableStateFlow(mutableListOf())
    val gyroList: StateFlow<MutableList<GyroInfo>>
        get() = _gyroList

    fun changeMeasureTarget() {
        when(_curMeasureTarget.value) {
            MeasureTarget.ACC -> {
                _curMeasureTarget.value = MeasureTarget.GYRO
            }
            MeasureTarget.GYRO -> {
                _curMeasureTarget.value = MeasureTarget.ACC
            }
        }
        Timber.tag(TAG).e("현재 측정 타겟 : ${_curMeasureTarget.value}")
    }

    companion object {
        private const val TAG = "MeasurementViewModel"
    }
}