package com.preonboarding.sensordashboard.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MeasurementEntity(

    // 임의의 데이터 등록하였습니다.
    @PrimaryKey(autoGenerate = true) var id: Int =0,
    val acc: String,
    val gyro: String
)
