package com.preonboarding.sensordashboard.domain.usecase

import com.preonboarding.sensordashboard.domain.model.SensorInfo
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import javax.inject.Inject

class SaveMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
){
    suspend operator fun invoke(sensorList: List<SensorInfo>, type: String, date: String, time: Double) =
        measurementRepository.saveMeasurement(
            sensorList = sensorList,
            type = type,
            date = date,
            time = time
        )
}