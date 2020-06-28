package com.bzahov.weatherapp.ui.weather.future.list

import android.util.Log
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.ui.base.states.AbstractWeatherState
import com.bzahov.weatherapp.ui.weather.future.list.recyclerview.FutureWeatherItem

data class FutureListState(
   override val weatherData: List<FutureDayData>,
   override val isMetric: Boolean
) : AbstractWeatherState(weatherData, isMetric) {
    init {
        initData()
    }
    override fun setDefaultErrorData() {
        Log.e("FutureListState", "weatherItems is empty or null")
        return
    }

    override fun calculateData() {
        weatherData.calculateWeatherItems()
    }

    lateinit var weatherItems :List<FutureWeatherItem>


    private fun List<FutureDayData>.calculateWeatherItems(){
        weatherItems = this.filter { it.dtTxt.contains(getAppString(R.string.default_future_time_calibration)) }
            .map { FutureWeatherItem(it, isMetric) }
            .apply { }
    }
}