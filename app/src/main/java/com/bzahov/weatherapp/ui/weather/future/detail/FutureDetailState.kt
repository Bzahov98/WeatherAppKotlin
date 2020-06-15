package com.bzahov.weatherapp.ui.weather.future.detail

import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.calculateWindDirectionToString

data class FutureDetailState(
    val weatherData: FutureDayData,
    val isMetric: Boolean,
    val timeZoneOffsetInSeconds: Int
) {

    var isDay: Boolean = false
    lateinit var iconNumber: String
    lateinit var detailWindText: String
    lateinit var detailVisibilityText: String
    lateinit var detailTemperatureText: String
    lateinit var detailFeelsLikeTemperatureText: String
    var rainPrecipitationText: String = ""
    lateinit var detailSubtitle: String
    lateinit var weatherConditionText: String

    init {
        calculateSubtitle()
        calculateCondition(weatherData.weatherDetails)
        iconNumber = weatherData.weatherDetails.last().icon
        calculatePrecipitation()
        calculateTemperature()
        calculateVisibility()
        calculateIsDay()
        calculateWind()
    }

    private fun calculateIsDay() {
        isDay = weatherData.sys.pod == getAppString(R.string.weather_open_is_day)
    }

    private fun calculateVisibility() {
        val unitAbbreviation = getAppString(R.string.percentage)
        detailVisibilityText =
            getAppString(R.string.weather_text_cloudiness) + " ${weatherData.clouds.all} $unitAbbreviation"
    }

    private fun calculateCondition(condList: List<WeatherDetails>) {
        weatherConditionText = UIConverterFieldUtils.getAllDescriptionsString(condList)
    }

    private fun calculateSubtitle() {
        detailSubtitle = UIConverterFieldUtils.dateTimestampToDateString(
            weatherData.dt,
            timeZoneOffsetInSeconds
        )
    }

    private fun calculatePrecipitation() {
        // always in mm
        val unitAbbreviation = getAppString(R.string.metric_precipitation)
        val snowVolume3h = weatherData.snow?.precipitationsForLast3hours
        val rainVolume3h = weatherData.rain?.precipitationsForLast3hours

        if (snowVolume3h ?: 0.0 > 0 && rainVolume3h ?: 0.0 > 0) {
            rainPrecipitationText =
                getAppString(R.string.weather_text_rain) +
                        " $rainVolume3h $unitAbbreviation, " +
                        getAppString(
                            R.string.weather_text_snow
                        ) +
                        ": $snowVolume3h"
            return
        } else rainPrecipitationText = "No precipitations"
        if (snowVolume3h ?: 0.0 > 0) {
            rainPrecipitationText =
                getAppString(R.string.weather_text_precipitation_snow) + " $snowVolume3h $unitAbbreviation"
        }
        if (rainVolume3h ?: 0.0 > 0) {
            rainPrecipitationText =
                getAppString(R.string.weather_text_precipitation_rain) + "$rainVolume3h $unitAbbreviation"
        }

    }

    private fun calculateTemperature() {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            isMetric,
            getAppString(R.string.metric_temperature),
            getAppString(R.string.imperial_temperature)
        )
        detailTemperatureText = "${weatherData.main.temp}$unitAbbreviation"
        detailFeelsLikeTemperatureText =
            getAppString(R.string.feels_like).plus("${weatherData.main.feelsLike}$unitAbbreviation")
    }

    private fun calculateWind() {
        detailWindText = calculateWindDirectionToString(weatherData.wind, isMetric, false)
    }
}