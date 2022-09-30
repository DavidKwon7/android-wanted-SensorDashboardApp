package com.preonboarding.sensordashboard.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity

@Dao
interface MeasurementDAO {

    @Query("SELECT * from measurements LIMIT :loadSize OFFSET (:page - 1) * :loadSize")
    suspend fun getAllMeasurement(page: Int, loadSize: Int): List<MeasurementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMeasurement(measurementEntity: MeasurementEntity)
}