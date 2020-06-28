package com.bzahov.weatherapp.ui.weather.future.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.ui.base.states.AbstractState
import com.bzahov.weatherapp.ui.base.states.EmptyState
import com.bzahov.weatherapp.ui.base.viewmodels.FutureWeatherViewModel
import java.time.LocalDateTime
import java.time.ZoneOffset

class FutureDetailWeatherViewModel(
    var detailDate: LocalDateTime,
    forecastRepository: FutureForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider,
    internetProvider: InternetProvider
) : FutureWeatherViewModel(forecastRepository, unitProvider,internetProvider, locationProvider) {
    private val TAG = "FutureDetailWeatherViewModel"
    private var detailDateTimeStamp: Long = 0L

    init {
        resetStartEndDates()
    }

    lateinit var weather: LiveData<FutureDayData>

    private val _uiViewsState = MutableLiveData<AbstractState>()
    var uiViewsState: LiveData<AbstractState> = _uiViewsState

    suspend fun getDetailData() {
        weather = forecastRepository.getFutureWeatherByDateTimestamp(detailDateTimeStamp)
//        if(weather.value == null ){
//            Log.e(TAG,"getDetailData FutureDayData is null")
//            return
//        }
        uiViewsState = Transformations.map(weather) {
            if (weather.value == null) {
                Log.e(TAG, "getCurrentWeather CurrentWeatherEntry is null")
                EmptyState()
            } else
                FutureDetailState(it, isMetric, getTimeZoneOffsetInSeconds())
        }
    }

    private fun resetStartEndDates() {
        detailDateTimeStamp =
            detailDate.toEpochSecond(ZoneOffset.ofTotalSeconds(getTimeZoneOffsetInSeconds()))
    }
}
