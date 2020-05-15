package com.bzahov.weatherapp.data.db

import androidx.room.TypeConverter
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

public class LocalDateConverter() {
    // LocalDateTime
    @TypeConverter
    fun stringToDateTime(string: String?) = string?.let {
        val dtFormatter =
            DateTimeFormatter.ofPattern(ForecastApplication.getAppString(R.string.date_formatter_from_string_pattern))
        //  val dateString = dateTime.format(dtFormatter)
        val result = LocalDateTime.parse(it, dtFormatter)
        return@let result
    }

    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime?): String? {
        val dtFormatter =
            DateTimeFormatter.ofPattern(ForecastApplication.getAppString(R.string.date_formatter_from_string_pattern))
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
