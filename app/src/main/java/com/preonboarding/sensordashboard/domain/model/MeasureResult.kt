package com.preonboarding.sensordashboard.domain.model

data class MeasureResult(
    val id: Int,
    val date: String,
    val measureTime: Double,
    val measureInfo: List<SensorInfo>,
    val measureType: String
)