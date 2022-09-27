package com.preonboarding.sensordashboard.data.repository

import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MeasurementRepositoryImpl @Inject constructor(
    private val measurementDao: MeasurementDAO
) : MeasurementRepository {

    override suspend fun getAllMeasurement(): Flow<List<MeasurementEntity>> = flow {
        measurementDao.getAllMeasurement()
    }
}