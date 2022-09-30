package com.preonboarding.sensordashboard.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.mapper.MeasurementMapper.mapToMeasureResult
import com.preonboarding.sensordashboard.domain.model.MeasureResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import timber.log.Timber

class MeasurementPagingSource(
    private val dao: MeasurementDAO
) : PagingSource<Int, MeasureResult>() {
    override fun getRefreshKey(state: PagingState<Int, MeasureResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MeasureResult> {

        val page = params.key ?: STARTING_KEY

        Timber.e("${params.key} // ${params.loadSize}")
        Timber.e("$page")
        if (page != STARTING_KEY) delay(1000L)

        return try {
            val measureResult = dao.getAllMeasurement(page, params.loadSize).mapToMeasureResult()
            Timber.e("$measureResult")

            LoadResult.Page(
                data = measureResult,
                prevKey = if (page == STARTING_KEY) null else page - 1,
                nextKey = if (measureResult.isEmpty()) null else page + 1
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val STARTING_KEY = 1
        private const val PAGING_DELAY = 1000L
    }
}