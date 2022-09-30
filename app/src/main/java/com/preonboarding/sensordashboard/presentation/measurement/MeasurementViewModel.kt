package com.preonboarding.sensordashboard.presentation.measurement

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preonboarding.sensordashboard.di.IoDispatcher
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import com.preonboarding.sensordashboard.domain.model.MeasureTarget
import com.preonboarding.sensordashboard.domain.usecase.SaveMeasurementUseCase
import com.preonboarding.sensordashboard.presentation.common.state.MeasurementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class MeasurementViewModel @Inject constructor(
    private val saveMeasurementUseCase: SaveMeasurementUseCase,
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

    private val _uiState: MutableStateFlow<MeasurementUiState> =
        MutableStateFlow(MeasurementUiState.Loading)
    val uiState: StateFlow<MeasurementUiState>
        get() = _uiState

    // 측정 or 정지중인지
    private val _isMeasuring: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isMeasuring: StateFlow<Boolean>
        get() = _isMeasuring

    // 측정 중 값 업데이트
    fun updateMeasurement(sensorInfo: SensorInfo) {
        _curSecond.value += SEC
        _sensorList.value.add(sensorInfo)
        Timber.tag(TAG).e(_curSecond.value.toString())
    }

    // 센서 타입 바뀌거나 저장하면 초기화
    fun clearMeasurementInfo() {
        _sensorList.value.clear()
        _curSecond.value = 0.0
    }

    fun setMeasureTarget(measureTarget: MeasureTarget) {
        _curMeasureTarget.value = measureTarget
    }

    fun setIsMeasuring(state: Boolean) {
        _isMeasuring.value = state
        Timber.tag(TAG).e(_isMeasuring.value.toString())
    }

    @SuppressLint("SimpleDateFormat")
    fun saveMeasurement() {
        // date
        val currentTime: Long = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date: String = sdf.format(currentTime)

        setIsMeasuring(false)

        viewModelScope.launch(dispatcher) {
            kotlin.runCatching {
                saveMeasurementUseCase(
                    sensorList = _sensorList.value,
                    type = _curMeasureTarget.value.type,
                    date = date,
                    time = curSecond.value
                )
            }
                .onSuccess {
                    _uiState.value = MeasurementUiState.SaveSuccess
                    clearMeasurementInfo()
                }
                .onFailure {
                    Timber.tag(TAG).e(it)
                    _uiState.value = MeasurementUiState.SaveFail
                }
        }
    }

    companion object {
        private const val TAG = "MeasurementViewModel"
        private const val SEC = 0.1
    }
}