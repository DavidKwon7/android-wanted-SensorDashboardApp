package com.preonboarding.sensordashboard

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import com.preonboarding.sensordashboard.presentation.dashboard.DashboardViewModel
import com.preonboarding.sensordashboard.utils.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class DashboardViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var measurementRepository: MeasurementRepository

    private lateinit var dashboardViewModel: DashboardViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        dashboardViewModel = DashboardViewModel(
            measurementRepository = measurementRepository,
            dispatcher = mainCoroutineRule.dispatcher
        )
    }
}