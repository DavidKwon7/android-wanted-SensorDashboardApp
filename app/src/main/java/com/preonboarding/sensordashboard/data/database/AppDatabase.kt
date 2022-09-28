package com.preonboarding.sensordashboard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.preonboarding.sensordashboard.data.converter.SensorListTypeConverter
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity

@Database(
    entities = [MeasurementEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(SensorListTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDAO
}