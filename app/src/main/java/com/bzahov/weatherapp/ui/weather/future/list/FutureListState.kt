package com.bzahov.weatherapp.ui.weather.future.list

import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.ui.weather.future.list.recyclerview.FutureWeatherItem

data class FutureListState(
    val weatherData: List<FutureDayData>,
    val isMetric: Boolean,
    val viewModel: FutureListWeatherViewModel
) {
    lateinit var weatherItems :List<FutureWeatherItem>
    init {
        weatherData.calculateWeatherItems()
    }

    private fun List<FutureDayData>.calculateWeatherItems(){
        weatherItems = this.filter { it.dtTxt.contains(getAppString(R.string.default_future_time_calibration)) }
            .map { FutureWeatherItem(it, viewModel.isMetric) }
            .apply { }
    }
}