package com.bzahov.weatherapp.data.db

import androidx.room.TypeConverter
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class LocalDateConverter() {
    // LocalDateTime
    @TypeConverter
    fun stringToDateTime(string: String?) = string?.let {
        val dtFormatter =
            DateTimeFormatter.ofPattern(getAppString(R.string.date_formatter_from_string_pattern))
        //  val dateString = dateTime.format(dtFormatter)
        return@let LocalDateTime.parse(it, dtFormatter)
    }

    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime?): String? {
        val dtFormatter =
            DateTimeFormatter.ofPattern(getAppString(R.string.date_formatter_from_string_pattern))
        return dateTime?.format(dtFormatter)
    }

    // LocalDate
    @TypeConverter
    fun dateToString(dateTime: LocalDate?) = dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @TypeConverter
    fun stringToDate(string: String?) = string?.let {
        LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
    }
}
