package com.bzahov.weatherapp.ui.weather.current

import android.util.Log
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.ui.base.states.AbstractWeatherState

data class CurrentWeatherState(
    override val weatherData: List<CurrentWeatherEntry>,
    override val isMetric: Boolean,
    override val timeZoneOffsetInSeconds: Int
) : AbstractWeatherState(weatherData, isMetric, timeZoneOffsetInSeconds) {

    constructor(
        weatherSingleData: CurrentWeatherEntry,
        isMetric: Boolean,
        timeZoneOffsetInSeconds: Int
    ) : this(listOf(weatherSingleData), isMetric, timeZoneOffsetInSeconds)

    lateinit var iconStringID: String
    val weatherSingleData: CurrentWeatherEntry
        get() {
            return weatherData.first()
        }

    var currentVisibility = errorString
    var currentCondition = errorString
    var currentPrecipitation = errorString
    var currentTemperature = errorString
    var currentFeelsLikeTemperature = errorString
    var currentWind = errorString
    var isDay: Boolean = false

    init {
        initData()
    }

    override fun setDefaultErrorData() {
        Log.e("CurrentWeatherState", "Error weather data is null or empty")
    }

    override fun calculateData() {
        calculateVisibility(weatherSingleData.visibility)
        calculateCondition(weatherSingleData.weatherDescriptions.toString())
        calculatePrecipitation(weatherSingleData.precipation)
        calculateTemperatures(weatherSingleData.temperature, weatherSingleData.feelslike)
        calculateWind(weatherSingleData.dir, weatherSingleData.speed)
        calculateIsDay(weatherSingleData.isDay)
        iconStringID = weatherSingleData.weatherIcons.last()
    }

    private fun calculateIsDay(day: String) {
        isDay = weatherSingleData.isDay == getAppString(R.string.weather_stack_is_day)
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

        currentCondition = weatherSingleData.getCondition()
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
                "weatherData=$weatherSingleData," +
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