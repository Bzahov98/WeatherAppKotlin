package com.bzahov.weatherapp.ui.base.states

import android.util.Log
import com.bzahov.weatherapp.data.db.entity.WeatherEntity

abstract class AbstractWeatherState(
    open val weatherData: List<WeatherEntity>,
    open val isMetric: Boolean,
    open val timeZoneOffsetInSeconds: Int = 0
) : AbstractState() {
    abstract fun setDefaultErrorData()

    abstract override fun calculateData()

//    init {
//        initData()
//    }

    protected fun initData() {
        if (weatherData.isNullOrEmpty()) {
            Log.e("AbstractWeatherState", "WeatherData is empty")
            setDefaultErrorData()
        } else {
            calculateData()
        }
    }

}