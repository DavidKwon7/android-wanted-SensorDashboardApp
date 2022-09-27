package com.preonboarding.sensordashboard.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccEntity(
    @PrimaryKey(autoGenerate = true) var id: Int =0,
    val x: Int,
    val y: Int,
    val z: Int
)

@Entity
data class GyroEntity(
    @PrimaryKey(autoGenerate = true)var id: Int =0,
    val x: Int,
    val y: Int,
    val z: Int
)

