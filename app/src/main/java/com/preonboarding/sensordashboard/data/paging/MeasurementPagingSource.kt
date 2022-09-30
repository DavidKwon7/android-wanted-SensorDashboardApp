package com.preonboarding.sensordashboard.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.preonboarding.sensordashboard.common.Constant.PAGING_DELAY
import com.preonboarding.sensordashboard.common.Constant.STARTING_KEY
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.domain.mapper.MeasurementMapper.mapToMeasureResult
import com.preonboarding.sensordashboard.domain.model.MeasureResult
import kotlinx.coroutines.delay

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

        if (page != STARTING_KEY) delay(PAGING_DELAY)

        return try {
            val measureResult = dao.getAllMeasurement(page, params.loadSize).mapToMeasureResult()

            LoadResult.Page(
                data = measureResult,
                prevKey = if (page == STARTING_KEY) null else page - 1,
                nextKey = if (measureResult.isEmpty()) null else page + 1
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}