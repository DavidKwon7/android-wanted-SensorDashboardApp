package com.preonboarding.sensordashboard.presentation.common.state

import com.preonboarding.sensordashboard.domain.model.SensorInfo

sealed class MeasurementUiState {
    object StartMeasurement : MeasurementUiState()
    object StopMeasurement : MeasurementUiState()
    data class SaveSuccess(val sensorInfo: SensorInfo) : MeasurementUiState()
    data class SaveFail(val error: String) : MeasurementUiState()
}