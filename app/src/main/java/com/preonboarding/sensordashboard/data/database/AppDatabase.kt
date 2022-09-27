package com.preonboarding.sensordashboard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.preonboarding.sensordashboard.data.converter.AccListTypeConverter
import com.preonboarding.sensordashboard.data.converter.GyroListTypeConverter
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.AccEntity
import com.preonboarding.sensordashboard.data.entity.GyroEntity
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity

@Database(
    entities = [MeasurementEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AccListTypeConverter::class, GyroListTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDAO
}