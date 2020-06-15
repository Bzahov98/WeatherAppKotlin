package com.bzahov.weatherapp.ui.weather.oneday

import android.util.Log
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.internal.OtherUtils
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.ui.weather.oneday.recyclerview.HourInfoItem

data class OneDayWeatherState(
    val weatherData: List<FutureDayData>,
    val isMetric: Boolean,
    val timeZoneOffsetInSeconds: Int
) {
    lateinit var currentPrecipitationText: String
    lateinit var oneDaySubtitle: String
    lateinit var hourInfoItemsList: List<HourInfoItem>
    lateinit var minMaxAvgTemp: MinMaxAvgTemp
    lateinit var allDayWeatherAndAverageData: MinMaxAvgTemp
    lateinit var allNightWeatherAndAverageData: MinMaxAvgTemp

    init {
        if (weatherData.isNullOrEmpty()) {
            Log.e("OneDayWeatherState", "weather data is empty")
        } else {
            convertToHourInfoItems()
            filterAllDayData()
            filterAllNightData()
            minMaxAvgTemp = MinMaxAvgTemp(weatherData, isMetric)
            allDayWeatherAndAverageData = MinMaxAvgTemp(filterAllDayData(), isMetric)
            allNightWeatherAndAverageData = MinMaxAvgTemp(filterAllNightData(), isMetric)
            calculateSubtitle()
        }
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
   private val isMetric: Boolean
) {

    private val unitAbbreviation: String = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
        isMetric,
        getAppString(R.string.metric_temperature),
        getAppString(R.string.imperial_temperature)
    )
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

    private var avgSumTemp: Double = 0.0
    private var avgSumFeelsTemp: Double = 0.0
    private var avgCount: Int = 0

    val iconViewConditionText
        get() = dataSource.random().weatherDetails.random().description

    val iconConditionID
        get() = dataSource.random().weatherDetails.random().icon

    init {
        calculateMinMaxAvgTemp()
        averageTemp = calculateAvrTemp()
        averageFeelLikeTemp = calculateAvrFeelsTemp()
    }

    private fun calculateAvrTemp(): Double {
        if (avgCount == 0) {
            return 0.0
        }
        return avgSumTemp / avgCount
    }

    private fun calculateAvrFeelsTemp(): Double {
        if (avgCount == 0) {
            return 0.0
        }
        return avgSumFeelsTemp / avgCount
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
}