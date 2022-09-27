package com.preonboarding.sensordashboard.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccInfo(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
)
