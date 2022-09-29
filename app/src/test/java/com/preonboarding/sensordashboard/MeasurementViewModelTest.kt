package com.preonboarding.sensordashboard

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
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
//@SmallTest
class MeasurementViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

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
    fun sensor_success() {
        runBlocking {
            measurementViewModel.sensorList.test {

                assertThat(expectItem()).isEqualTo(TestDataGenerator.generateSensorInfo())
                expectComplete()
            }
        }
    }

    /*@Test
    suspend fun cur_measure_target_success() {
        measurementViewModel.curMeasureTarget.test {
            coEvery { measurementViewModel.setMeasureTarget(any()) } returns Unit
            //measurementViewModel.setMeasureTarget()
            //Truth.assertThat(expectItem()).isEqualTo(1)
            val data = expectItem()
            Truth.assertThat(data).isEqualTo("GYRO")
        }
        //measurementViewModel.setMeasureTarget(any())
    }*/
}