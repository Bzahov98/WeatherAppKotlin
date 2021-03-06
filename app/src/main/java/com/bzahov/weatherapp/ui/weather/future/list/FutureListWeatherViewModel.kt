package com.bzahov.weatherapp.ui.weather.future.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.internal.enums.UnitSystem
import com.bzahov.weatherapp.internal.lazyDeferred
import com.bzahov.weatherapp.ui.base.states.AbstractState
import com.bzahov.weatherapp.ui.base.states.EmptyState
import java.time.LocalDate
private const val TAG = "FutureListWeatherViewModel"
class FutureListWeatherViewModel(
    private val forecastRepository: FutureForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider,
    private val internetProvider: InternetProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()
    private val locationSystem = locationProvider.getLocationString()

    val isMetric: Boolean
        get() = unitSystem.urlOpenWeatherToken == UnitSystem.METRIC.urlOpenWeatherToken
    val location: String
        get() = locationSystem

    lateinit var weather: LiveData<List<FutureDayData>>
    private val _uiViewsState = MutableLiveData<AbstractState>()
    var uiViewsState: LiveData<AbstractState> = _uiViewsState

    /*val forecastWeather by lazyDeferred {
        Log.d(TAG,"forecastWeatherDefered")
        forecastRepository.getFutureWeather(LocalDate.now())
    }*/

    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }

    suspend fun requestRefreshOfData() {
        forecastRepository.requireRefreshOfData = true
        forecastRepository.requestRefreshOfData()
    }

    suspend fun getFutureListData() {
        weather = forecastRepository.getFutureWeather(LocalDate.now())
//        if(weather.value == null ){
//            Log.e(TAG,"getFutureListData List<FutureDayData> is null")
//            return
//        }
        uiViewsState = Transformations.map(weather) {
            if (weather.value == null) {
                Log.e(TAG, "getCurrentWeather CurrentWeatherEntry is null")
                EmptyState()
            } else
            FutureListState(it, isMetric)
        }
    }
    fun isOnline(): Boolean  = internetProvider.isNetworkConnected
}
