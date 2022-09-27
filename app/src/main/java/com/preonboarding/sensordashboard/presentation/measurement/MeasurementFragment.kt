package com.preonboarding.sensordashboard.presentation.measurement

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.preonboarding.sensordashboard.R
import com.preonboarding.sensordashboard.databinding.FragmentMeasurementBinding
import com.preonboarding.sensordashboard.presentation.MainActivity
import com.preonboarding.sensordashboard.presentation.common.base.BaseFragment
import com.preonboarding.sensordashboard.presentation.common.util.NavigationUtil.navigateUp

class MeasurementFragment : BaseFragment<FragmentMeasurementBinding>(R.layout.fragment_measurement) {

    private lateinit var mainActivity: MainActivity


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = context as MainActivity
        initViews()

    }

    private fun initViews() {
        binding.tbMeasurement.setNavigationOnClickListener {
            navigateUp()
        }
        binding.apply {
            val thread = ThreadClass()
            thread.start()
        }

    }
    inner class ThreadClass : Thread() {
        override fun run() {
            val input = Array<Double>(100,{Math.random()})
            // Entry 배열 생성
            var entries: ArrayList<Entry> = ArrayList()
            // Entry 배열 초기값 입력
            entries.add(Entry(0F , 0F))
            // 그래프 구현을 위한 LineDataSet 생성
            var dataset: LineDataSet = LineDataSet(entries, "input")
            // 그래프 data 생성 -> 최종 입력 데이터
            var data: LineData = LineData(dataset)
            // chart.xml에 배치된 lineChart에 데이터 연결
            binding.measurementLineChart.data = data

            mainActivity.runOnUiThread {
                // 그래프 생성
                binding.measurementLineChart.animateXY(1, 1)
            }

            for (i in 0 until input.size){

                SystemClock.sleep(10)
                data.addEntry(Entry(i.toFloat(), input[i].toFloat()), 0)
                data.notifyDataChanged()
                binding.measurementLineChart.notifyDataSetChanged()
                binding.measurementLineChart.invalidate()
            }

        }
    }
}