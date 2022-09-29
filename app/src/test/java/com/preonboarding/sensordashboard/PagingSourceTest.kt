package com.preonboarding.sensordashboard

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.ClipData
import androidx.paging.PagingSource
import app.cash.turbine.Event
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.paging.MeasurementPagingSource
import com.preonboarding.sensordashboard.utils.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(JUnit4::class)
class PagingSourceTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var measurementDAO: MeasurementDAO

    private lateinit var measurementPagingSource: MeasurementPagingSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        measurementPagingSource = MeasurementPagingSource(measurementDAO)
    }

    @Test
    fun paging_source_load_failure_received_io_exception() =
        mainCoroutineRule.runBlockingTest {
            val error = IOException("404", Throwable())

            coEvery { measurementDAO.getAllMeasurement() } throws error

            val expectedResult = PagingSource.LoadResult.Error<Int, ClipData.Item>(error)

            Assert.assertEquals(
                expectedResult, measurementPagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                )
            )
        }

    @Test
    fun paging_source_load_failure_received_null_exception() =
        mainCoroutineRule.runBlockingTest {
            coEvery { measurementDAO.getAllMeasurement() } throws NullPointerException()

            val expectedResult = PagingSource.LoadResult.Error<Int, ClipData.Item>(NullPointerException())

            Assert.assertEquals(
                expectedResult.toString(), measurementPagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                ).toString()
            )
        }
}