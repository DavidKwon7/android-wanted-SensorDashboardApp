package com.preonboarding.sensordashboard.data.repository

import androidx.annotation.WorkerThread
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.preonboarding.sensordashboard.domain.model.GyroInfo
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
        accList: List<AccInfo>?,
        gyroList: List<GyroInfo>?,
        date: String
    ) {
        measurementDao.saveMeasurement(MeasurementEntity(accList = accList, gyroList = gyroList, date = date))
    }
}