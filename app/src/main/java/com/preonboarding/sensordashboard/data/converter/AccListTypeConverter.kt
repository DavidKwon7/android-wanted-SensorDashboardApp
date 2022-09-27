package com.preonboarding.sensordashboard.data.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.preonboarding.sensordashboard.domain.model.AccInfo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

@ProvidedTypeConverter
class AccListTypeConverter @Inject constructor(
    private val moshi: Moshi
) {
    private val listType = Types.newParameterizedType(List::class.java, AccInfo::class.java)
    private val adapter: JsonAdapter<List<AccInfo>> = moshi.adapter(listType)

    // string -> list로 DB에서 가져오기
    @TypeConverter
    fun fromString(value: String): List<AccInfo>? {
        return if(value.isEmpty()) {
            listOf()
        } else {
            adapter.fromJson(value)
        }
    }


    // list -> string으로 DB에 보내기
    @TypeConverter
    fun fromAccList(type: List<AccInfo>): String {
        return adapter.toJson(type)
    }

}