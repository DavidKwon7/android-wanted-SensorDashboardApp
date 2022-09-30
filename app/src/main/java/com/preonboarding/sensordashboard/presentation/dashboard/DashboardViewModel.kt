package com.preonboarding.sensordashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.preonboarding.sensordashboard.domain.model.MeasureResult
import com.preonboarding.sensordashboard.domain.usecase.GetAllMeasurementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllMeasurementUseCase: GetAllMeasurementUseCase
) : ViewModel() {

    // 전체 측정 데이터
    private val _measureData: MutableStateFlow<PagingData<MeasureResult>> =
        MutableStateFlow(PagingData.empty())
    val measureData: StateFlow<PagingData<MeasureResult>> = _measureData.asStateFlow()


    fun getAllMeasurement() {
        viewModelScope.launch {
            getAllMeasurementUseCase.invoke()
                .cachedIn(viewModelScope)
                .collectLatest { measureList ->
                    _measureData.emit(measureList)
                }
        }
    }
}