package com.preonboarding.sensordashboard.presentation.measurement

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.preonboarding.sensordashboard.di.IoDispatcher
import com.preonboarding.sensordashboard.domain.model.MeasureResult
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import com.preonboarding.sensordashboard.domain.model.MeasureTarget
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
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

    // 측정 or 정지중인지
    private val _isMeasuring: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isMeasuring: StateFlow<Boolean>
        get() = _isMeasuring

    // 저장 잘 됐는지
    private val _saveState: MutableStateFlow<Boolean> =
        MutableStateFlow(true)
    val saveState: StateFlow<Boolean>
        get() = _saveState

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

        Timber.tag(TAG).d("[저장]\ntype : ${_curMeasureTarget.value.type}\nsensorList : ${_sensorList.value}\ndate: $date\ntime : ${_curSecond.value}")
        Timber.tag(TAG).d("데이터 개수 : ${_sensorList.value.size}")

        setIsMeasuring(false)

        viewModelScope.launch(dispatcher) {
            kotlin.runCatching {
                measurementRepository.saveMeasurement(
                    sensorList = _sensorList.value,
                    type = _curMeasureTarget.value.type,
                    date = date,
                    time = curSecond.value
                )
            }
                .onSuccess {
                    Timber.tag(TAG).e("저장 성공")
                    _saveState.value = true
                    clearMeasurementInfo()
                }
                .onFailure {
                    Timber.tag(TAG).e(it)
                    _saveState.value = false
                }
        }
    }

    companion object {
        private const val TAG = "MeasurementViewModel"
        private const val SEC = 0.1
    }
}