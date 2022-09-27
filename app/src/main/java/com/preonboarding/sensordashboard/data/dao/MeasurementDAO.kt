package com.preonboarding.sensordashboard.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDAO {

    @Query("SELECT * from MEASUREMENTS")
    fun getAllMeasurement(): Flow<List<MeasurementEntity>>
}