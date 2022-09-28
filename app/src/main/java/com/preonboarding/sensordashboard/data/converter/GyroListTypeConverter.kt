package com.preonboarding.sensordashboard.data.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.preonboarding.sensordashboard.domain.model.GyroInfo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

@ProvidedTypeConverter
class GyroListTypeConverter @Inject constructor(
    private val moshi: Moshi
) {
    private val listType = Types.newParameterizedType(List::class.java, GyroInfo::class.java)
    private val adapter: JsonAdapter<List<GyroInfo>> = moshi.adapter(listType)

    // string -> list로 DB에서 가져오기
    @TypeConverter
    fun fromString(value: String): List<GyroInfo>? {
        return if(value.isEmpty()) {
            listOf()
        } else {
            adapter.fromJson(value)
        }
    }

    // list -> string으로 DB에 보내기
    @TypeConverter
    fun fromGyroList(type: List<GyroInfo>): String {
        return adapter.toJson(type)
    }
}