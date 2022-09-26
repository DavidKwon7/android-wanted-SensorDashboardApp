package com.preonboarding.sensordashboard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.preonboarding.sensordashboard.domain.entity.MeasurementEntity

@Database(
    entities = [MeasurementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDAO
}