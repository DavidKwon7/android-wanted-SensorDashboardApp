package com.preonboarding.sensordashboard.domain.repository

import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.preonboarding.sensordashboard.domain.model.GyroInfo
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    suspend fun getAllMeasurement(): Flow<List<MeasurementEntity>>
    suspend fun saveMeasurement(accList: List<AccInfo>?, gyroList: List<GyroInfo>?, date: String)
}