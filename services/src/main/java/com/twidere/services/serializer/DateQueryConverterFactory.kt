package com.twidere.services.serializer

import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DateQueryConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit?
    ): Converter<*, String?>? {
        return if (type == Date::class.java) {
            DateQueryConverter.INSTANCE
        } else {
            null
        }
    }

    private class DateQueryConverter : Converter<Date?, String?> {
        val simpleDateFormat by lazy {
            SimpleDateFormat("yyyy-MM-DDTHH:mm:ssZ", Locale.ENGLISH)
        }
        override fun convert(date: Date?): String? {
            return if (date == null) {
                null
            } else {
                simpleDateFormat.format(date)
            }
        }
        companion object {
            val INSTANCE = DateQueryConverter()
        }
    }
}