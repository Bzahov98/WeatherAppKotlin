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
import java.time.ZoneOffset

class OneDayWeatherViewModel(
    private val forecastRepository: FutureForecastRepository,
    val unitProvider: UnitProvider,
    private val locationProvider: LocationProvider
) : FutureWeatherViewModel(forecastRepository, unitProvider, locationProvider) {

    val TAG = "OneDayWeatherViewModel"

    init {
        resetStartEndDates()
    }

    lateinit var startDateQuery: LocalDateTime /*= LocalDate.now().atStartOfDay() // LocalDateTime.now().minusHours(2).minusMinutes(55)*/
    private lateinit var endDateQuery: LocalDateTime/*= LocalDate.now().atTime(LocalTime.MAX).minusMinutes(4)/*LocalDateTime = LocalDateTime.of(
        startDateQuery.toLocalDate(),
        LocalDate.now().atTime(LocalTime.MAX).toLocalTime()*/
      // LocalDate.now().atTime(LocalTime.MAX)*/

    val weather by lazyDeferred {
        Log.e(
            TAG,
            "getFutureWeatherByDateTime with startDate $startDateQuery and endDate $endDateQuery"
        )

        val startDateLong = startDateQuery.toEpochSecond(
            ZoneOffset.ofTotalSeconds(
                locationProvider.offsetDateTime
            )
        )
        val endDateLong =
            endDateQuery.toEpochSecond(ZoneOffset.ofTotalSeconds(locationProvider.offsetDateTime))

        return@lazyDeferred forecastRepository.getFutureWeatherByStartAndEndDate(
            startDateLong,
            endDateLong
        )
    }

    fun resetStartEndDates() {

        startDateQuery = LocalDate.now().plusDays(1).atStartOfDay()


        endDateQuery = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusDays(2)
        Log.e(TAG, "resetDates with startDate $startDateQuery and endDate $endDateQuery\n")
    }
    /*fun resetStartEndDates() {
        Log.e(TAG, "resetDates with startDate $startDateQuery and endDate $endDateQuery")
        startDateQuery = LocalDateTime.now().minusHours(2).minusMinutes(55)
        endDateQuery = LocalDate.now().atTime(LocalTime.MAX)
    }*/

    fun hasUnitSystemChanged() = unitProvider.hasUnitSystemChanged()
}
