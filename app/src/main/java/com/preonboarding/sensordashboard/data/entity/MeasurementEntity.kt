package com.preonboarding.sensordashboard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.preonboarding.sensordashboard.domain.model.GyroInfo
import com.squareup.moshi.JsonClass

@Entity(tableName = "measurements")
@JsonClass(generateAdapter = true)
data class MeasurementEntity(
    val accList: List<AccInfo>? = null,
    val gyroList: List<GyroInfo>? = null,
    val date: String,
) {
    @PrimaryKey(autoGenerate = true) var id: Int =0
}


