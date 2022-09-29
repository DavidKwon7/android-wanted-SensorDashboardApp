package com.preonboarding.sensordashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.preonboarding.sensordashboard.di.IoDispatcher
import com.preonboarding.sensordashboard.domain.model.MeasureResult
import com.preonboarding.sensordashboard.domain.repository.MeasurementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    // 전체 측정 데이터
    private val _measureData: MutableStateFlow<PagingData<MeasureResult>> =
        MutableStateFlow<PagingData<MeasureResult>>(PagingData.empty())
    val measureData: StateFlow<PagingData<MeasureResult>> = _measureData.asStateFlow()


    fun getAllMeasurement() {
        Timber.e("START")
        viewModelScope.launch(dispatcher) {
            measurementRepository.getAllMeasurement()
                .cachedIn(viewModelScope)
                .collectLatest { measureList ->
                    _measureData.emit(measureList)
                }
        }
    }
}