package com.preonboarding.sensordashboard

import android.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.data.repository.MeasurementRepositoryImpl
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import com.preonboarding.sensordashboard.utils.MainCoroutineRule
import com.preonboarding.sensordashboard.utils.TestDataGenerator
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException
import java.lang.Error
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(JUnit4::class)
class MeasurementRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

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

    @Test
    fun test_get_data_success() = runTest {
        val data = TestDataGenerator.generateMeasurementEntityList()

        coEvery { measurementDAO.getAllMeasurement() } returns data

        val expectData = measurementDAO.getAllMeasurement()

        coVerify { measurementDAO.getAllMeasurement() }

        Assert.assertEquals(data, expectData)
    }

    @Test
    fun test_save_measurement_success() = runTest {
        val saveItem = TestDataGenerator.generateSensorInfoList()

        coEvery { measurementDAO.saveMeasurement(any()) } returns Unit

        val returned = measurementRepository.saveMeasurement(saveItem,"a","a",1.1)

        coVerify { measurementDAO.saveMeasurement(any()) }

        Truth.assertThat(returned).isEqualTo(Unit)
    }

    @Test(expected = Exception::class)
    fun test_save_measurement_fail() = runTest {
        val saveItem = TestDataGenerator.generateSensorInfoList()

        coEvery { measurementDAO.saveMeasurement(any()) } throws Exception()

        measurementRepository.saveMeasurement(saveItem,"a","a",1.1)

        coVerify { measurementDAO.saveMeasurement(any()) }

    }

}