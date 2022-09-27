package com.preonboarding.sensordashboard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.preonboarding.sensordashboard.data.converter.AccListTypeConverter
import com.preonboarding.sensordashboard.data.converter.GyroListTypeConverter
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.preonboarding.sensordashboard.domain.model.GyroInfo
import com.squareup.moshi.JsonClass

@Entity(tableName = "measurements")
@JsonClass(generateAdapter = true)
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true) var id: Int =0,
    val accList: List<AccInfo>? = null,
    val gyroList: List<GyroInfo>? =null,
    val date: String,
)


