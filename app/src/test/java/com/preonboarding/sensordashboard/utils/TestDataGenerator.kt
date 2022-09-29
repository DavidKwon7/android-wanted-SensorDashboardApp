package com.preonboarding.sensordashboard.utils

import com.preonboarding.sensordashboard.data.entity.MeasurementEntity
import com.preonboarding.sensordashboard.domain.model.MeasureResult
import com.preonboarding.sensordashboard.domain.model.SensorInfo

class TestDataGenerator {

    companion object {

        fun generateMeasurementEntity(): MeasurementEntity {
            return MeasurementEntity(generateSensorInfoList(), "Accelerometer", "2022/09/29 04:49:36", 60.00003)
        }

        fun generateSensorInfo(): SensorInfo {
            return SensorInfo(1,2,1)
        }

        fun generateMeasurementEntityList(): List<MeasurementEntity> {
            val item1 = MeasurementEntity(generateSensorInfoList(), "Accelerometer", "2022/09/29 04:49:36",60.00003)
            val item2 = MeasurementEntity(generateSensorInfoList(), "Accelerometer", "2022/09/29 04:49:36",60.00003)
            val item3 = MeasurementEntity(generateSensorInfoList(), "Accelerometer", "2022/09/29 04:49:36",60.00003)
            return listOf(item1, item2, item3)
        }

        fun generateSensorInfoList(): List<SensorInfo> {
            val item1 = SensorInfo(1,1,1,)
            val item2 = SensorInfo(2,2,2)
            val item3 = SensorInfo(3,3,3)
            return listOf(item1, item2, item3)
        }

        fun generateMeasureResultList(): List<MeasurementEntity> {
            val item1 = MeasurementEntity(generateSensorInfoList(), "Accelerometer", "2022/09/29 04:49:36", 60.00003)
            val item2 = MeasurementEntity(generateSensorInfoList(), "Accelerometer", "2022/09/29 04:49:36", 60.00003)
            val item3 = MeasurementEntity(generateSensorInfoList(), "Accelerometer", "2022/09/29 04:49:36", 60.00003)
            return listOf(item1, item2, item3)
        }
    }
}