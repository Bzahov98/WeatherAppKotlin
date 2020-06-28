package com.bzahov.weatherapp.ui.weather.current

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.internal.enums.UnitSystem
import com.bzahov.weatherapp.internal.lazyDeferred
import com.bzahov.weatherapp.ui.base.states.AbstractState
import com.bzahov.weatherapp.ui.base.states.EmptyState

private val TAG = "CurrentWeatherViewModel"

class CurrentWeatherViewModel(
    private val currentForecastRepository: CurrentForecastRepository,
    unitProvider: UnitProvider,
    val locationProvider: LocationProvider,
    val internetProvider: InternetProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    lateinit var weather: LiveData<CurrentWeatherEntry>

    private val _uiViewsState = MutableLiveData<AbstractState>()
    var uiViewsState: LiveData<AbstractState> = _uiViewsState


    suspend fun getCurrentWeather() {
        weather = currentForecastRepository.getCurrentWeather()
        /*if(weather.value == null){
            Log.e(TAG,"getCurrentWeather CurrentWeatherEntry is null")

            return
        }*/
        uiViewsState = Transformations.map(weather) {
            if (weather.value == null) {
                Log.e(TAG, "getCurrentWeather CurrentWeatherEntry is null")
                EmptyState()
            } else
                CurrentWeatherState(it, isMetric, locationProvider.offsetDateTime)
        }
    }

    val weatherLocation by lazyDeferred {
        currentForecastRepository.getWeatherLocation()
    }

    suspend fun requestRefreshOfData() {

        currentForecastRepository.requestRefreshOfData()
    }

    fun isOnline(): Boolean = internetProvider.isNetworkConnected
}