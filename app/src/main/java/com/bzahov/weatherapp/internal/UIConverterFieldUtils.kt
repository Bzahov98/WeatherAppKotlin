package com.bzahov.weatherapp.internal

import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class UIConverterFieldUtils {
    companion object {
        fun getOpenWeatherIconUrl(iconNumber: String): String {
            return ForecastApplication.getAppString(R.string.weather_open_icon_url) +
                    iconNumber +
                    ForecastApplication.getAppString(
                        R.string.image_format_png
                    )
        }

        fun dateTimestampToDateTimeString(dtTimestamp: Long): String {
            val dtPattern = ForecastApplication.getAppString(R.string.date_formatter_pattern)
            return dateTimestampToString(dtTimestamp,dtPattern)
        }


        fun dateTimestampToTimeString(dtTimestamp: Long): String {
            val dtPattern = ForecastApplication.getAppString(R.string.date_formatter_pattern_hour_minutes_only)
            return dateTimestampToString(dtTimestamp,dtPattern)
        }

        fun dateTimestampToDateString(dtTimestamp: Long): String {
            val dtPattern = ForecastApplication.getAppString(R.string.date_formatter_pattern_day_month__only)
            return dateTimestampToString(dtTimestamp,dtPattern)
        }

        // REWORK: Fix ZoneId.systemDefault() to real data zone
        fun dateTimestampToString(dtTimestamp: Long, pattern: String): String {
            val dtFormatter = DateTimeFormatter.ofPattern(
                pattern
            )
            val dateTime =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(dtTimestamp), ZoneId.systemDefault())
            return dateTime.format(dtFormatter)
        }

        fun getAllDescriptionsString(weatherDetails: List<WeatherDetails>): String {
            var cond = StringBuilder()
            weatherDetails.forEach() { cond.append(it.description).append(" ") }
            return cond.toString()
        }
        fun chooseLocalizedUnitAbbreviation(isMetricUnit: Boolean,metricAbbr: String, imperialAbbr: String): String {
            return if (isMetricUnit) metricAbbr else imperialAbbr
        }
    }
}