package com.preonboarding.sensordashboard.utils

import com.preonboarding.sensordashboard.domain.model.SensorInfo

class TestDataGenerator {

    companion object {

        fun generateList(): List<SensorInfo> {
            val item1 = SensorInfo(1,1,1,)
            val item2 = SensorInfo(2,2,2)
            val item3 = SensorInfo(3,3,3)
            return listOf(item1, item2, item3)
        }
    }
}