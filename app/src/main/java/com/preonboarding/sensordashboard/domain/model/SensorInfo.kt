package com.preonboarding.sensordashboard.domain.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SensorInfo(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) : Parcelable {
    companion object {
        fun emptyInfo() = SensorInfo(0, 0, 0)
    }
}
