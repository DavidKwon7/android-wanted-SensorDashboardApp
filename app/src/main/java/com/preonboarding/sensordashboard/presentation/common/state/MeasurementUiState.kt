package com.preonboarding.sensordashboard.presentation.common.state


sealed class MeasurementUiState {
    object Loading: MeasurementUiState()
    object SaveSuccess: MeasurementUiState()
    object SaveFail: MeasurementUiState()
}