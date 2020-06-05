package com.bzahov.weatherapp.internal

import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails
import com.bzahov.weatherapp.internal.enums.WindDirections
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class UIConverterFieldUtils {
    companion object {
        fun getOpenWeatherIconUrl(iconNumber: String): String {
            return getAppString(R.string.weather_open_icon_url) +
                    iconNumber +
                    getAppString(
                        R.string.image_format_png
                    )
        }

        fun dateTimestampToDateTimeString(dtTimestamp: Long, offsetTotalSeconds: Int = 0): String {
            val dtPattern = getAppString(R.string.date_formatter_pattern)
            return dateTimestampToString(dtTimestamp, offsetTotalSeconds, dtPattern)
        }


        fun dateTimestampToTimeString(dtTimestamp: Long, offsetTotalSeconds: Int = 0): String {
            val dtPattern = getAppString(R.string.date_formatter_pattern_hour_minutes_only)
            return dateTimestampToString(dtTimestamp, offsetTotalSeconds, dtPattern)
        }

        fun dateTimestampToDateString(dtTimestamp: Long, offsetTotalSeconds: Int = 0): String {
            val dtPattern = getAppString(R.string.date_formatter_pattern_day_month__only)
            return dateTimestampToString(dtTimestamp, offsetTotalSeconds, dtPattern)
        }

        fun dateTimestampToString(
            dtTimestamp: Long,
            offsetTotalSeconds: Int = 0,
            pattern: String
        ): String {
            val dtFormatter = DateTimeFormatter.ofPattern(pattern)
            val dateTime =
                LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(dtTimestamp),
                    ZoneOffset.ofTotalSeconds(offsetTotalSeconds)
                )
            return dateTime.format(dtFormatter)
        }

        fun getAllDescriptionsString(weatherDetails: List<WeatherDetails>): String {
            val cond = StringBuilder()
            weatherDetails.forEach() { cond.append(it.description).append(" ") }
            return cond.toString()
        }

        fun chooseLocalizedUnitAbbreviation(
            isMetricUnit: Boolean,
            metricAbbr: String,
            imperialAbbr: String
        ): String {
            return if (isMetricUnit) metricAbbr else imperialAbbr
        }

        fun convertDoubleToWindDirectionString(windDirection: Double): String {
            return WindDirections.getDescriptionStringByDouble(windDirection)
        }

        fun convertWindDirectionToShortString(windDirection: Double): String {
            return WindDirections.getShortDescriptionStringByDouble(windDirection)
        }

        fun convertDoubleValueAndAbbreviationToString(tempMin: Double, unitAbbreviation: String) =
            "${String.format("%.1f", tempMin)}$unitAbbreviation"
    }
}