package com.preonboarding.sensordashboard

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.preonboarding.sensordashboard.domain.usecase.DeleteMeasurementUseCase
import com.preonboarding.sensordashboard.domain.usecase.GetAllMeasurementUseCase
import com.preonboarding.sensordashboard.presentation.dashboard.DashboardViewModel
import com.preonboarding.sensordashboard.utils.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DashboardViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var getAllMeasurementUseCase: GetAllMeasurementUseCase

    @MockK
    private lateinit var deleteMeasurementUseCase: DeleteMeasurementUseCase

    private lateinit var dashboardViewModel: DashboardViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        dashboardViewModel = DashboardViewModel(
            getAllMeasurementUseCase = getAllMeasurementUseCase,
            deleteMeasurementUseCase = deleteMeasurementUseCase
        )
    }

    @Test
    fun get_All_MeasurementData_Fail() = runTest {

        coEvery { getAllMeasurementUseCase.invoke() } throws Exception()

        dashboardViewModel.getAllMeasurement()

        coVerify { getAllMeasurementUseCase.invoke() }
    }
}