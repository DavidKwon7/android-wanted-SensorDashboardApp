package com.preonboarding.sensordashboard.domain.repository

import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    suspend fun getAllMeasurement(): Flow<List<MeasurementEntity>>
    suspend fun saveMeasurement(sensorList: List<SensorInfo>?, type: String, date: String, time: Double)
}