package com.bzahov.weatherapp.internal

import android.view.View
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class UIConverterFieldUtils {
    companion object {
        fun getWeatherIconUrl(iconNumber: String, view: View): String {
            return ForecastApplication.getAppString(R.string.weather_open_icon_url) +
                    iconNumber + view.context.getString(
                R.string.image_format_png
            )
        }

        fun dateTimestampToString(dtTimestamp:Long,view: View) : String{
            val dtFormatter = DateTimeFormatter.ofPattern(view.context.getString(R.string.date_formatter_pattern))
            val dateTime =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(dtTimestamp), ZoneId.systemDefault())
            return dateTime.format(dtFormatter)
        }

        fun getAllDescriptionsString(weatherDetails: List<WeatherDetails>): String {
            var cond = StringBuilder()
            weatherDetails.forEach() { cond.append(it.description).append(" ") }
            return cond.toString()
        }
    }
}