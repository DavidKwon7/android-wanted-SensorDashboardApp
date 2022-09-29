package com.preonboarding.sensordashboard.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.preonboarding.sensordashboard.data.dao.MeasurementDAO
import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.mapper.MeasurementMapper.mapToMeasureResult
import com.preonboarding.sensordashboard.domain.model.MeasureResult
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
        val page = params.key ?: 0

        return try {
            val measureResult = dao.getAllMeasurement().mapToMeasureResult()
            Timber.e("$measureResult")

            LoadResult.Page(
                data = measureResult,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (measureResult.isEmpty()) null else page + 1
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}