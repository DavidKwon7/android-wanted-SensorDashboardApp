package com.preonboarding.sensordashboard.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeasureResult(
    val id: Int,
    val date: String,
    val measureTime: Double,
    val measureInfo: List<SensorInfo>,
    val measureType: String
) : Parcelable