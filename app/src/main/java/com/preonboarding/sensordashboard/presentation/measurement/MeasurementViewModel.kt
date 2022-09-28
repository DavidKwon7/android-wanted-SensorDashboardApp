package com.preonboarding.sensordashboard.presentation.measurement

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preonboarding.sensordashboard.di.IoDispatcher
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import com.preonboarding.sensordashboard.domain.model.MeasureTarget
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class MeasurementViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    // Acc or Gyro 타입
    private val _curMeasureTarget: MutableStateFlow<MeasureTarget> =
        MutableStateFlow(MeasureTarget.ACC)
    val curMeasureTarget: StateFlow<MeasureTarget>
        get() = _curMeasureTarget

    // 센서 값 리스트
    private val _sensorList: MutableStateFlow<MutableList<SensorInfo>> =
        MutableStateFlow(mutableListOf())
    val sensorList: StateFlow<MutableList<SensorInfo>>
        get() = _sensorList

    // 측정 시간 (초)
    private val _curSecond: MutableStateFlow<Double> =
        MutableStateFlow(0.0)
    val curSecond: StateFlow<Double>
        get() = _curSecond

    // 센서 타입 바뀌면 초기화
    fun clearSensorList() {
        _sensorList.value.clear()
    }

    fun setMeasureTarget(measureTarget: MeasureTarget) {
        _curMeasureTarget.value = measureTarget
    }

    @SuppressLint("SimpleDateFormat")
    fun saveMeasurement() {
        val currentTime: Long = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date: String = sdf.format(currentTime)

        Timber.tag(TAG).d("[저장]\ntype : ${_curMeasureTarget.value.type}\nsensorList : ${_sensorList.value}\ndate: $date\ntime : ${_curSecond.value}")

        viewModelScope.launch(dispatcher) {
            kotlin.runCatching {
                measurementRepository.saveMeasurement(
                    sensorList = _sensorList.value,
                    type = _curMeasureTarget.value.type,
                    date = date,
                    time = _curSecond.value
                )
            }
                .onSuccess {
                    Timber.tag(TAG).e("저장 성공")
                }
                .onFailure {
                    Timber.tag(TAG).e(it)
                }
        }
    }

    companion object {
        private const val TAG = "MeasurementViewModel"
    }
}