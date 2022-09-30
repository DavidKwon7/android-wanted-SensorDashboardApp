package com.preonboarding.sensordashboard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import com.squareup.moshi.JsonClass

@Entity(tableName = "measurements")
@JsonClass(generateAdapter = true)
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sensorList: List<SensorInfo>? = null,
    val type: String,
    val date: String,
    val time: Double = 0.0,
)