package com.preonboarding.sensordashboard.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.preonboarding.sensordashboard.databinding.ItemSensorResultBinding
import com.preonboarding.sensordashboard.domain.model.MeasureResult

class DashboardPagingAdapter(
    private val optionClicked: () -> Unit,
    private val itemClicked: (MeasureResult) -> Unit
) : PagingDataAdapter<MeasureResult, DashboardPagingAdapter.DashboardViewHolder>(
        DASHBOARD_DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSensorResultBinding.inflate(inflater, parent, false)
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bindItems(it, optionClicked, itemClicked)
        }
    }

    class DashboardViewHolder(private val binding: ItemSensorResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(
            item: MeasureResult,
            optionClicked: () -> Unit,
            itemClicked: (MeasureResult) -> Unit
        ) = with(binding) {
            measureResult = item

            ivOption.setOnClickListener {
                optionClicked.invoke()
            }

            clMeasureContainer.setOnClickListener {
                itemClicked.invoke(item)
            }

            executePendingBindings()
        }

    }

    companion object {
        private val DASHBOARD_DIFF_CALLBACK = object : DiffUtil.ItemCallback<MeasureResult>() {
            override fun areItemsTheSame(
                oldItem: MeasureResult,
                newItem: MeasureResult
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MeasureResult,
                newItem: MeasureResult
            ) = oldItem == newItem
        }
    }
}