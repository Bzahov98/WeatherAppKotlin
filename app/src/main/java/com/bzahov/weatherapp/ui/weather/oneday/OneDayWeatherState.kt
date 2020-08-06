package com.bzahov.weatherapp.ui.weather.oneday

import android.util.Log
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.internal.OtherUtils
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.ui.base.states.AbstractWeatherState
import com.bzahov.weatherapp.ui.weather.oneday.recyclerview.HourInfoItem

private val TAG = "OneDayWeatherState"

data class OneDayWeatherState(
    override val weatherData: List<FutureDayData>,
    override val isMetric: Boolean,
    override val timeZoneOffsetInSeconds: Int
) : AbstractWeatherState(weatherData, isMetric, timeZoneOffsetInSeconds) {

    var currentPrecipitationText: String = errorString
    var oneDaySubtitle: String = errorString
    lateinit var hourInfoItemsList: List<HourInfoItem>
    lateinit var minMaxAvgTemp: MinMaxAvgTemp
    lateinit var allDayWeatherAndAverageData: MinMaxAvgTemp
    lateinit var allNightWeatherAndAverageData: MinMaxAvgTemp

    init {
        initData()
    }

    override fun calculateData() {
        convertToHourInfoItems()
        filterAllDayData()
        filterAllNightData()
        minMaxAvgTemp = MinMaxAvgTemp(weatherData, isMetric)
        allDayWeatherAndAverageData = MinMaxAvgTemp(filterAllDayData(), isMetric)
        allNightWeatherAndAverageData = MinMaxAvgTemp(filterAllNightData(), isMetric)
        calculateSubtitle()
    }

    override fun setDefaultErrorData() {
        Log.e(TAG, errorString)
        currentPrecipitationText = errorString
        oneDaySubtitle = errorString
        hourInfoItemsList = emptyList()
        minMaxAvgTemp = MinMaxAvgTemp()
        allDayWeatherAndAverageData = MinMaxAvgTemp()
        allNightWeatherAndAverageData = MinMaxAvgTemp()
    }

    private fun filterAllDayData() =
        weatherData.filter(OtherUtils.isDayTime(timeZoneOffsetInSeconds))

    private fun filterAllNightData() =
        weatherData.filterNot(OtherUtils.isDayTime(timeZoneOffsetInSeconds))

    private fun convertToHourInfoItems() {
        hourInfoItemsList = weatherData.map {
            HourInfoItem(it, isMetric, timeZoneOffsetInSeconds)
        }.apply { }
    }

    private fun calculateSubtitle() {
        oneDaySubtitle = UIConverterFieldUtils.dateTimestampToDateString(
            weatherData[0].dt,
            timeZoneOffsetInSeconds
        )
    }

    private fun calculatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
            isMetric,
            getAppString(R.string.metric_precipitation),
            getAppString(R.string.imperial_precipitation)
        )
        currentPrecipitationText =
            getAppString(R.string.weather_text_precipitation) + " $precipitationVolume $unitAbbreviation"
    }
}

class MinMaxAvgTemp(
    private val dataSource: List<FutureDayData>,
    isMetric: Boolean
) {
    constructor() : this(emptyList(), true)


    private var avgSumTemp: Double = 0.0
    private var avgSumFeelsTemp: Double = 0.0
    private var avgCount: Int = 0

    var minTemp: Double = 100.0
    val minTempText
        get() = String.format("%.1f $unitAbbreviation", minTemp)

    var maxTemp: Double = 0.0
    val maxTempText
        get() = String.format("%.1f $unitAbbreviation", maxTemp)

    var averageTemp: Double = 0.0
    val averageTempText
        get() = String.format("%.1f $unitAbbreviation", averageTemp)

    var averageFeelLikeTemp: Double = 0.0
    val averageFeelLikeTempText
        get() = String.format("%.1f $unitAbbreviation", averageFeelLikeTemp)

    val iconViewConditionText: String
        get() {
            return if (dataSource.isNotEmpty()) {
                dataSource.random().weatherDetails.random().description
            } else {
                getAppString(R.string.error_no_data_warning)
            }
        }

    val iconConditionID: String
        get() {
            return if (dataSource.isNotEmpty()) {
                dataSource.random().weatherDetails.random().icon
            } else {
                getAppString(R.string.error_no_data_warning)
            }
        }

    private fun calculateAvrFeelsTemp(): Double {
        if (avgCount == 0) {
            return 0.0
        }
        return avgSumFeelsTemp / avgCount
    }

    init {
        if (dataSource.isNotEmpty()) {
            calculateMinMaxAvgTemp()
            averageTemp = calculateAvrTemp()
            averageFeelLikeTemp = calculateAvrFeelsTemp()
        } else {
            Log.e(TAG, "MinMaxAvgTemp list is empty")
        }

    }

    private fun calculateMinMaxAvgTemp() {
        dataSource.forEach {
            avgCount++
            avgSumTemp += it.main.temp
            avgSumFeelsTemp += it.main.feelsLike

            if (maxTemp < it.main.temp) {
                maxTemp = it.main.temp
            }
            if (minTemp > it.main.temp) {
                minTemp = it.main.temp
            }
        }
    }

    private fun calculateAvrTemp(): Double {
        if (avgCount == 0) {
            return 0.0
        }
        return avgSumTemp / avgCount
    }

    private val unitAbbreviation: String = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
        isMetric,
        getAppString(R.string.metric_temperature),
        getAppString(R.string.imperial_temperature)
    )
}