package com.bzahov.weatherapp.ui.weather.current

import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.internal.UIConverterFieldUtils

data class CurrentWeatherState(val weatherData: CurrentWeatherEntry, val isMetric: Boolean) {

    lateinit var currentVisibility: String
    lateinit var currentCondition: String
    lateinit var currentPrecipitation: String
    lateinit var currentTemperature: String
    lateinit var currentFeelsLikeTemperature: String
    lateinit var currentWind: String
    var isDay: Boolean = false

    init {
        calculateVisibility(weatherData.visibility)
        calculateCondition(weatherData.weatherDescriptions.toString())
        calculatePrecipitation(weatherData.precipation)
        calculateTemperatures(weatherData.temperature, weatherData.feelslike)
        calculateWind(weatherData.dir, weatherData.speed)
        calculateIsDay(weatherData.isDay)
    }

    private fun calculateIsDay(day: String) {
        isDay = weatherData.isDay == getAppString(R.string.weather_stack_is_day)
    }

    private fun calculateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
            isMetric,
            getAppString(R.string.metric_speed),
            getAppString(R.string.imperial_speed)
        )
        currentWind =
            getAppString(R.string.weather_text_wind) + "$windDirection, $windSpeed $unitAbbreviation"
    }

    private fun calculateTemperatures(temperature: Double, feelsLike: Double) {
        val unitAbbreviation = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
            isMetric,
            getAppString(R.string.metric_temperature),
            getAppString(R.string.imperial_temperature)
        )
        currentTemperature = "$temperature$unitAbbreviation"
        currentFeelsLikeTemperature =
            getAppString(R.string.weather_text_feels_like) + " $feelsLike$unitAbbreviation"
    }

    private fun calculatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
            isMetric,
            getAppString(R.string.metric_precipitation),
            getAppString(R.string.imperial_precipitation)
        )
        currentPrecipitation =
            getAppString(R.string.weather_text_precipitation) + " $precipitationVolume $unitAbbreviation"
    }

    private fun calculateCondition(condition: String) {
        currentCondition = condition.removePrefix("[").removeSuffix("]")
    }

    private fun calculateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
            isMetric,
            getAppString(R.string.metric_distance),
            getAppString(R.string.imperial_distance)
        )
        currentVisibility =
            getAppString(R.string.weather_text_visibility) + "$visibilityDistance $unitAbbreviation"
    }

    override fun toString(): String {
        return "CurrentWeatherState(" +
                "weatherData=$weatherData," +
                "isMetric=$isMetric" +
                "\n>UI DATA:     " +
                "currentVisibility='$currentVisibility', " +
                "currentCondition='$currentCondition', " +
                "currentPrecipitation='$currentPrecipitation', " +
                "currentTemperature='$currentTemperature', " +
                "currentFeelsLikeTemperature='$currentFeelsLikeTemperature', " +
                "currentWind='$currentWind', " +
                "isDay=$isDay" +
                ")\n"
    }
}