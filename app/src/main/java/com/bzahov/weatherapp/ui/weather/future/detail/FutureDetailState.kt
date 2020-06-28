package com.bzahov.weatherapp.ui.weather.future.detail

import android.util.Log
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.calculateWindDirectionToString
import com.bzahov.weatherapp.ui.base.states.AbstractWeatherState

class FutureDetailState(
    override val weatherData: List<FutureDayData>,
    override val isMetric: Boolean,
    override val timeZoneOffsetInSeconds: Int
) : AbstractWeatherState(weatherData, isMetric, timeZoneOffsetInSeconds) {

    constructor(
        weatherSingleData: FutureDayData,
        isMetric: Boolean,
        timeZoneOffsetInSeconds: Int
    ) : this(listOf(weatherSingleData), isMetric, timeZoneOffsetInSeconds)

    var isDay: Boolean = false
    var iconNumber: String = errorString
    var detailWindText: String = errorString
    var detailVisibilityText: String = errorString
    var detailTemperatureText: String = errorString
    var detailFeelsLikeTemperatureText: String = errorString
    var rainPrecipitationText: String = errorString
    var detailSubtitle: String = errorString
    var weatherConditionText: String = errorString

    init {
        initData()
    }

    val weatherSingleData: FutureDayData
        get() {
            return weatherData.first()
        }

    override fun setDefaultErrorData() {
        Log.e("FutureDetail", "Error weather data is null or empty")
    }


    override fun calculateData() {
        Log.e("FutureDetail", "calculate data")

        calculateSubtitle()
        calculateCondition(weatherSingleData.weatherDetails)
        iconNumber = weatherSingleData.weatherDetails.last().icon
        calculatePrecipitation()
        calculateTemperature()
        calculateVisibility()
        calculateIsDay()
        calculateWind()
    }


    private fun calculateIsDay() {
        isDay = weatherSingleData.sys.pod == getAppString(R.string.weather_open_is_day)
    }

    private fun calculateVisibility() {
        val unitAbbreviation = getAppString(R.string.percentage)
        detailVisibilityText =
            getAppString(R.string.weather_text_cloudiness) + " ${weatherSingleData.clouds.all} $unitAbbreviation"
    }

    private fun calculateCondition(condList: List<WeatherDetails>) {
        weatherConditionText = UIConverterFieldUtils.getAllDescriptionsString(condList)
    }

    private fun calculateSubtitle() {
        detailSubtitle = UIConverterFieldUtils.dateTimestampToDateString(
            weatherSingleData.dt,
            timeZoneOffsetInSeconds
        )
    }

    private fun calculatePrecipitation() {
        // always in mm
        val unitAbbreviation = getAppString(R.string.metric_precipitation)
        val snowVolume3h = weatherSingleData.snow?.precipitationsForLast3hours
        val rainVolume3h = weatherSingleData.rain?.precipitationsForLast3hours

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
        detailTemperatureText = "${weatherSingleData.main.temp}$unitAbbreviation"
        detailFeelsLikeTemperatureText =
            getAppString(R.string.feels_like).plus("${weatherSingleData.main.feelsLike}$unitAbbreviation")
    }

    private fun calculateWind() {
        detailWindText = calculateWindDirectionToString(weatherSingleData.wind, isMetric, false)
    }
}