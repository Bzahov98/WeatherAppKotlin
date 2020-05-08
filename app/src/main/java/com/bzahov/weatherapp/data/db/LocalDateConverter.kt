package com.bzahov.weatherapp.data.db

import androidx.room.TypeConverter
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateConverter {
    // LocalDateTime
    @TypeConverter
    @JvmStatic
    fun stringToDateTime(string: String?) = string?.let {
        val dtFormatter =
            DateTimeFormatter.ofPattern(ForecastApplication.getAppString(R.string.date_from_string_formatter_pattern))
        //  val dateString = dateTime.format(dtFormatter)
        LocalDateTime.parse(it, dtFormatter)
    }

    @JvmStatic
    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime?): String? {
        val dtFormatter =
            DateTimeFormatter.ofPattern(ForecastApplication.getAppString(R.string.date_formatter_pattern))
        return dateTime?.format(dtFormatter)
    }
    // LocalDate

    @TypeConverter
    @JvmStatic
    fun dateToString(dateTime: LocalDate?) = dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @TypeConverter
    @JvmStatic
    fun stringToDate(string: String?) = string?.let {
        LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
    }

}
