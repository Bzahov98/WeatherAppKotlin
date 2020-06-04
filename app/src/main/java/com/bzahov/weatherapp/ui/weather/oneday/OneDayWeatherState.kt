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
    val viewModel: OneDayWeatherViewModel
) {
    //    lateinit var allDayWeatherData: List<FutureDayData>
//    lateinit var allNightWeatherData: List<FutureDayData>
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
            minMaxAvgTemp = MinMaxAvgTemp(weatherData, viewModel.isMetric)
            allDayWeatherAndAverageData = MinMaxAvgTemp(filterAllDayData(), viewModel.isMetric)
            allNightWeatherAndAverageData = MinMaxAvgTemp(filterAllNightData(), viewModel.isMetric)

            calculateSubtitle()
        }
    }

    private fun filterAllDayData() =
        weatherData.filter(OtherUtils.isDayTime(viewModel.getTimeZoneOffsetInSeconds()))


    private fun filterAllNightData() =
        weatherData.filterNot(OtherUtils.isDayTime(viewModel.getTimeZoneOffsetInSeconds()))

    private fun convertToHourInfoItems() {
        hourInfoItemsList = weatherData.map {
            HourInfoItem(it, viewModel.isMetric, viewModel.getTimeZoneOffsetInSeconds())
        }.apply { }
    }

    private fun calculateSubtitle() {
        oneDaySubtitle = UIConverterFieldUtils.dateTimestampToDateString(
            weatherData[0].dt,
            viewModel.getTimeZoneOffsetInSeconds()
        )
    }
}

class MinMaxAvgTemp(
    val dataSource: List<FutureDayData>,
    val isMetric: Boolean
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

    fun calculateAvrTemp(): Double {
        if (avgCount == 0) {
            return 0.0
        }
        return avgSumTemp / avgCount
    }

    fun calculateAvrFeelsTemp(): Double {
        if (avgCount == 0) {
            return 0.0
        }
        return avgSumFeelsTemp / avgCount
    }

    fun calculateMinMaxAvgTemp() {
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