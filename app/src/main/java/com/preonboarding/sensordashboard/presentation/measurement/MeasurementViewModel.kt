package com.preonboarding.sensordashboard.presentation.measurement

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preonboarding.sensordashboard.di.IoDispatcher
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.preonboarding.sensordashboard.domain.model.GyroInfo
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
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): ViewModel() {

    private val _curMeasureTarget: MutableStateFlow<MeasureTarget> =
        MutableStateFlow(MeasureTarget.ACC)
    val curMeasureTarget: StateFlow<MeasureTarget>
        get() = _curMeasureTarget

    private val _accList: MutableStateFlow<MutableList<AccInfo>> =
        MutableStateFlow(mutableListOf())
    val accList: StateFlow<MutableList<AccInfo>>
        get() = _accList

    private val _accSecond: MutableStateFlow<Int> =
        MutableStateFlow(0)
    val accSecond: StateFlow<Int>
        get() = _accSecond

    private val _gyroList: MutableStateFlow<MutableList<GyroInfo>> =
        MutableStateFlow(mutableListOf())
    val gyroList: StateFlow<MutableList<GyroInfo>>
        get() = _gyroList

    private val _gyroSecond: MutableStateFlow<Int> =
        MutableStateFlow(0)
    val gyroSecond: StateFlow<Int>
        get() = _gyroSecond

    fun setMeasureTarget(measureTarget: MeasureTarget) {
        _curMeasureTarget.value = measureTarget
    }

    @SuppressLint("SimpleDateFormat")
    fun saveMeasurement() {
        val currentTime: Long = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date: String = sdf.format(currentTime)

        Timber.tag(TAG).d("[저장]\nacc : ${_accList.value}\ngyro : ${_gyroList.value}\ndate: $date")

        viewModelScope.launch(dispatcher) {
            kotlin.runCatching {
                measurementRepository.saveMeasurement(
                    accList = _accList.value,
                    gyroList = _gyroList.value,
                    date = date
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