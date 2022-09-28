package com.preonboarding.sensordashboard.data.repository

import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.model.SensorInfo
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

    override suspend fun saveMeasurement(
        sensorList: List<SensorInfo>?,
        type: String,
        date: String,
        time: Double
    ) {
        measurementDao.saveMeasurement(MeasurementEntity(sensorList = sensorList, type = type, date = date, time = time))
    }
}