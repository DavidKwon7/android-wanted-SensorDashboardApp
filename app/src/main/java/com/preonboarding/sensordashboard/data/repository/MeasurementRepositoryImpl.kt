package com.preonboarding.sensordashboard.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.data.paging.MeasurementPagingSource
import com.preonboarding.sensordashboard.domain.model.MeasureResult
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MeasurementRepositoryImpl @Inject constructor(
    private val measurementDao: MeasurementDAO
) : MeasurementRepository {

    override suspend fun getAllMeasurement(): Flow<PagingData<MeasureResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { MeasurementPagingSource(measurementDao) }
        ).flow
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