package com.preonboarding.sensordashboard

import android.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.preonboarding.sensordashboard.domain.model.MeasureTarget
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import com.preonboarding.sensordashboard.presentation.measurement.MeasurementViewModel
import com.preonboarding.sensordashboard.utils.MainCoroutineRule
import com.preonboarding.sensordashboard.utils.TestDataGenerator
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MeasurementViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var measurementRepository: MeasurementRepository

    private lateinit var measurementViewModel: MeasurementViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        measurementViewModel = MeasurementViewModel(
            measurementRepository = measurementRepository,
            dispatcher = mainCoroutineRule.dispatcher
        )
    }

    // sample
    @Test
    fun testSample() {
        runBlocking {
            flow {
                emit("test")
                emit("test")
            }.test {
                assertThat(expectItem()).isEqualTo("test")
                assertThat(expectItem()).isEqualTo("test")
                expectComplete()
            }
        }
    }

    @Test
    fun sensor_fail() = runTest {
        val data = TestDataGenerator.generateSensorInfo()
        coEvery { measurementRepository.saveMeasurement(any(), any(), any(), any()) } throws Exception()

        measurementViewModel.saveMeasurement()

        coVerify { measurementRepository.saveMeasurement(any(), any(), any(), any()) }

    }

    // todo 수정 필요
    @Test
    fun sensor_success() = runTest {
        val data = TestDataGenerator.generateSensorInfo()
        coEvery { measurementRepository.saveMeasurement(any(), any(), any(), any()) } returns Unit

        measurementViewModel.sensorList.test {
            measurementRepository.saveMeasurement(
                date = "data",
                sensorList = listOf(data),
                type = "ACC",
                time = 13.00,
            )
            Truth.assertThat(expectItem()).isEqualTo(data)
        }
        coVerify { measurementRepository.saveMeasurement(any(), any(), any(), any()) }
    }
}


