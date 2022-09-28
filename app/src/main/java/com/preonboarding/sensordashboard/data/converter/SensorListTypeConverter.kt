package com.preonboarding.sensordashboard.data.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.preonboarding.sensordashboard.domain.model.SensorInfo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

@ProvidedTypeConverter
class SensorListTypeConverter @Inject constructor(
    private val moshi: Moshi
) {
    private val listType = Types.newParameterizedType(List::class.java, SensorInfo::class.java)
    private val adapter: JsonAdapter<List<SensorInfo>> = moshi.adapter(listType)

    // string -> list로 DB에서 가져오기
    @TypeConverter
    fun fromString(value: String): List<SensorInfo>? {
        return if(value.isEmpty()) {
            listOf()
        } else {
            adapter.fromJson(value)
        }
    }


    // list -> string으로 DB에 보내기
    @TypeConverter
    fun fromAccList(type: List<SensorInfo>): String {
        return adapter.toJson(type)
    }

}