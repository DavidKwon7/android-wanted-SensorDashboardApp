package com.preonboarding.sensordashboard.domain.repository

import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    suspend fun getAllMeasurement(): Flow<List<MeasurementEntity>>
}