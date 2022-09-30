package com.preonboarding.sensordashboard.domain.usecase

import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke() =
        measurementRepository.getAllMeasurement().flowOn(Dispatchers.Default)

}