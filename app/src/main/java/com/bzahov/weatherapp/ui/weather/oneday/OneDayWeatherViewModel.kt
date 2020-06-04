package com.bzahov.weatherapp.ui.weather.oneday

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.ui.base.FutureWeatherViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class OneDayWeatherViewModel(
    forecastRepository: FutureForecastRepository,
    val unitProvider: UnitProvider,
    private val locationProvider: LocationProvider
) : FutureWeatherViewModel(forecastRepository, unitProvider, locationProvider) {
    lateinit var startDateQuery: LocalDateTime
    private lateinit var endDateQuery: LocalDateTime
    val TAG = "OneDayWeatherViewModel"

    init {
        resetStartEndDates()
    }

    lateinit var weather: LiveData<List<FutureDayData>>

    private val _uiViewsState = MutableLiveData<OneDayWeatherState>()
    var uiViewsState: LiveData<OneDayWeatherState> = _uiViewsState

    suspend fun getOneDayData() {
        Log.d(
            TAG,
            "getFutureWeatherByDateTime with startDate $startDateQuery and endDate $endDateQuery"
        )

        val startDateLong = startDateQuery.toEpochSecond(
            ZoneOffset.ofTotalSeconds(
                locationProvider.offsetDateTime
            )
        )
        val endDateLong =
            endDateQuery.minusSeconds(1)
                .toEpochSecond(ZoneOffset.ofTotalSeconds(locationProvider.offsetDateTime))

        weather = forecastRepository.getFutureWeatherByStartAndEndDate(
            startDateLong,
            endDateLong
        )
        requestRefreshOfData()
        uiViewsState = Transformations.map(weather){
            OneDayWeatherState(it,isMetric,this)
        }
    }

    /*val weather by lazyDeferred {
        Log.d(
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
    }*/

    // REWORK: now show data for the next day (additionDays = 1)
    //  (if current data for today is fetched after one time, entities before that time missing )
    fun resetStartEndDates(addAdditionDays: Long = 1L) {
        startDateQuery = LocalDate.now().plusDays(addAdditionDays).atStartOfDay()
        endDateQuery = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusDays(addAdditionDays + 1)
        Log.d(TAG, "resetDates with startDate $startDateQuery and endDate $endDateQuery\n")
    }
}