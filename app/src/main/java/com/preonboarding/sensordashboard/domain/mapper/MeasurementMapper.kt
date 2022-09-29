package com.preonboarding.sensordashboard.domain.mapper

import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.model.MeasureResult

object MeasurementMapper {

    fun List<MeasurementEntity>.mapToMeasureResult(): List<MeasureResult> {
        val measureResultList = mutableListOf<MeasureResult>()
        this.map { entity ->
            measureResultList.add(
                MeasureResult(
                    id = entity.id,
                    date = entity.date,
                    measureTime = entity.time,
                    measureInfo = entity.sensorList ?: emptyList(),
                    measureType = entity.type
                )
            )
        }

        return measureResultList
    }
}