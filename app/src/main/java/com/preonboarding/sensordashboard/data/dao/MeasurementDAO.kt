package com.preonboarding.sensordashboard.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity

@Dao
interface MeasurementDAO {

    @Query("SELECT * from MEASUREMENTS")
    suspend fun getAllMeasurement(): List<MeasurementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMeasurement(measurementEntity: MeasurementEntity)
}