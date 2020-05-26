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

    lateinit var startDateQuery: LocalDateTime
    private lateinit var endDateQuery: LocalDateTime

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
            endDateQuery.minusSeconds(1).toEpochSecond(ZoneOffset.ofTotalSeconds(locationProvider.offsetDateTime))

        return@lazyDeferred forecastRepository.getFutureWeatherByStartAndEndDate(
            startDateLong,
            endDateLong
        )
    }

    fun resetStartEndDates(addAdditionDays : Long = 1L) {
        startDateQuery = LocalDate.now().plusDays(addAdditionDays).atStartOfDay()
        endDateQuery = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusDays(addAdditionDays+1)
        Log.e(TAG, "resetDates with startDate $startDateQuery and endDate $endDateQuery\n")
    }
}
