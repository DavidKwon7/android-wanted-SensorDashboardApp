package com.preonboarding.sensordashboard.domain.usecase

import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import javax.inject.Inject

class DeleteMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke(id: Int) = measurementRepository.deleteMeasurementById(id)
}