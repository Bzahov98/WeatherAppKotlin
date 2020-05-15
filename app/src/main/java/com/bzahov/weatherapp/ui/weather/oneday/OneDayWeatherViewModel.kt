package com.bzahov.weatherapp.ui.weather.oneday

import android.util.Log
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.internal.lazyDeferred
import com.bzahov.weatherapp.ui.base.FutureWeatherViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class OneDayWeatherViewModel(
    private val forecastRepository: FutureForecastRepository,
    private val unitProvider: UnitProvider,
    private val locationProvider: LocationProvider
): FutureWeatherViewModel(forecastRepository,unitProvider,locationProvider) {

    val TAG = "OneDayWeatherViewModel"

    var startDateQuery :LocalDateTime = LocalDateTime.now().minusHours(2).minusMinutes(55)
    private var endDateQuery :LocalDateTime = LocalDateTime.of(startDateQuery.toLocalDate(),LocalDate.now().atTime(LocalTime.MAX).toLocalTime())  // LocalDate.now().atTime(LocalTime.MAX)

    init {
        resetStartEndDates()
    }

    val weather by lazyDeferred {
        Log.e(TAG, "getFutureWeatherByDate with startDate $startDateQuery and endDate $endDateQuery")
        return@lazyDeferred forecastRepository.getFutureWeatherByStartAndEndDate(startDateQuery, endDateQuery)
    }

    fun resetStartEndDates(){
        Log.e(TAG, "resetDates with startDate $startDateQuery and endDate $endDateQuery")
        startDateQuery = LocalDateTime.now().minusHours(2).minusMinutes(55)
        endDateQuery= LocalDate.now().atTime(LocalTime.MAX)
    }
}
