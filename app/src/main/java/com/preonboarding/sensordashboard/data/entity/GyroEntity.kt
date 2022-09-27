package com.preonboarding.sensordashboard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class GyroEntity(
    @PrimaryKey(autoGenerate = true) var id: Int =0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0
)

