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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException
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


    @OptIn(ExperimentalTime::class)
    @Test
    fun test_get_data_success() = runTest {
        val data = TestDataGenerator.generateMeasurementEntityList()

        coEvery { measurementDAO.getAllMeasurement() } returns data

        val flow = measurementRepository.getAllMeasurement()

        flow.test {
            val expected = expectItem()

            Truth.assertThat(expected).isEqualTo(data)
        }
    }


    //test1_fail_case
    @Test
    fun test_fail() = runTest {
        coEvery { measurementDAO.getAllMeasurement() } throws Exception()
        val api = measurementRepository.getAllMeasurement()
        Truth.assertThat(api).isEqualTo(TestDataGenerator.generateMeasurementEntityList())
        coVerify { measurementDAO.getAllMeasurement() }
    }

/*    // todo edit this code => flow 로 인해 데이터 확인을 못 하고 있음 ..
    @Test
    fun test_get_all_measurement_success() = runTest {
        val getItems = TestDataGenerator.generateMeasurementEntityList()
        coEvery { measurementDAO.getAllMeasurement() } returns(getItems)
        val returned = measurementRepository.getAllMeasurement()
        //coVerify { measurementDAO.getAllMeasurement() }
        //Truth.assertThat(returned).containsExactlyElementsIn(getItems)
        Truth.assertThat(returned).isEqualTo(flowData())
    }

    @Test(expected = Exception::class)
    fun test_get_all_measurement_fail() = runTest {
        coEvery { measurementDAO.getAllMeasurement() } throws Exception()
        measurementRepository.getAllMeasurement()
        coVerify { measurementDAO.getAllMeasurement() }
    }

    // return 값이 Uni 이여서 일단 Unit 처리
    @Test
    fun test_save_measurement_success() = runTest {

        val saveItem = TestDataGenerator.generateSensorInfoList()

        coEvery { measurementDAO.saveMeasurement(any()) } returns Unit

        val returned = measurementRepository.saveMeasurement(saveItem,"a","a",1.1)

        coVerify { measurementDAO.saveMeasurement(any()) }

        // assertion
        Truth.assertThat(returned).isEqualTo(Unit)
    }

    @Test(expected = Exception::class)
    fun test_save_measurement_fail() = runTest {

        val saveItem = TestDataGenerator.generateSensorInfoList()

        // Given
        coEvery { measurementDAO.saveMeasurement(any()) } throws Exception()

        // When
        measurementRepository.saveMeasurement(saveItem,"a","a",1.1)

        // Then
        coVerify { measurementDAO.saveMeasurement(any()) }

    }*/

}