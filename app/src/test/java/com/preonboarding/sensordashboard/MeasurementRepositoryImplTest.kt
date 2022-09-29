package com.preonboarding.sensordashboard

import com.google.common.truth.Truth
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.repository.MeasurementRepositoryImpl
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import com.preonboarding.sensordashboard.utils.TestDataGenerator
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class MeasurementRepositoryImplTest {

    @MockK
    private lateinit var measurementDAO: MeasurementDAO

    private lateinit var measurementRepository: MeasurementRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        measurementRepository = MeasurementRepositoryImpl(
            measurementDao = measurementDAO
        )
    }

    // return 값이 Uni 이여서 일단 Unit 처리
    @Test
    fun test_save_measurement_success() = runTest {

        val saveItem = TestDataGenerator.generateList()

        coEvery { measurementDAO.saveMeasurement(any()) } returns Unit

        val returned = measurementRepository.saveMeasurement(saveItem,"a","a",1.1)

        coVerify { measurementDAO.saveMeasurement(any()) }

        // assertion
        Truth.assertThat(returned).isEqualTo(Unit)
    }

    @Test(expected = Exception::class)
    fun test_save_measurement_fail() = runTest {

        val saveItem = TestDataGenerator.generateList()

        // Given
        coEvery { measurementDAO.saveMeasurement(any()) } throws Exception()

        // When
        measurementRepository.saveMeasurement(saveItem,"a","a",1.1)

        // Then
        coVerify { measurementDAO.saveMeasurement(any()) }

    }
}