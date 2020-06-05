package com.bzahov.weatherapp.ui.weather.future.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.ui.base.FutureWeatherViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset

class FutureDetailWeatherViewModel(
    var detailDate: LocalDateTime,
    forecastRepository: FutureForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider
) : FutureWeatherViewModel(forecastRepository, unitProvider, locationProvider) {
    private val TAG = "FutureDetailWeatherViewModel"
    private var detailDateTimeStamp: Long = 0L

    init {
        resetStartEndDates()
    }

    lateinit var weather: LiveData<FutureDayData>

    private val _uiViewsState = MutableLiveData<FutureDetailState>()
    var uiViewsState: LiveData<FutureDetailState> = _uiViewsState
    /*val weather by lazyDeferred {
        Log.e(TAG,"getFutureWeatherByDate with detailDate $detailDateTimeStamp")
        return@lazyDeferred forecastRepository.getFutureWeatherByDateTimestamp(detailDateTimeStamp)
    }*/

    suspend fun getDetailData() {
        weather = forecastRepository.getFutureWeatherByDateTimestamp(detailDateTimeStamp)
        uiViewsState = Transformations.map(weather) {
            FutureDetailState(it, isMetric, this)
        }
    }

    private fun resetStartEndDates() {
        detailDateTimeStamp =
            detailDate.toEpochSecond(ZoneOffset.ofTotalSeconds(getTimeZoneOffsetInSeconds()))
    }
}
