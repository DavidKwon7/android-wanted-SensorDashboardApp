package com.preonboarding.sensordashboard.domain.repository

import androidx.paging.PagingData
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.model.MeasureResult
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    suspend fun getAllMeasurement(): Flow<PagingData<MeasureResult>>
    suspend fun saveMeasurement(sensorList: List<SensorInfo>?, type: String, date: String, time: Double)
    suspend fun deleteMeasurementById(id: Int)
}