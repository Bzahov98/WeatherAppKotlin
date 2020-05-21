package com.bzahov.weatherapp.ui.weather.future.detail

import android.util.Log
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.internal.lazyDeferred
import com.bzahov.weatherapp.ui.base.FutureWeatherViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset

class FutureDetailWeatherViewModel(
   var detailDate: LocalDateTime,
    forecastRepository: FutureForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider
) : FutureWeatherViewModel(forecastRepository,unitProvider,locationProvider) {
    val TAG = "FutureDetailWeatherViewModel"

    init {
        resetStartEndDates()
    }

    private fun resetStartEndDates() {
        detailDateTimeStapm = detailDate.toEpochSecond(ZoneOffset.ofTotalSeconds(0))
    }

    var detailDateTimeStapm: Long = 0

    val weather by lazyDeferred {
            Log.e(TAG,"getFutureWeatherByDate with detailDate $detailDateTimeStapm")
         return@lazyDeferred forecastRepository.getFutureWeatherByDateTimestamp(detailDateTimeStapm, super.isMetric)
    }
}
