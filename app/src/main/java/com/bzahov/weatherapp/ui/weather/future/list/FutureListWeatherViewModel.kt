package com.bzahov.weatherapp.ui.weather.future.list

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.internal.enums.UnitSystem
import com.bzahov.weatherapp.internal.lazyDeferred
import java.time.LocalDate
private const val TAG = "FutureListWeatherViewModel"
class FutureListWeatherViewModel(
    private val forecastRepository: FutureForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()
    private val locationSystem = locationProvider.getLocationString()

    val isMetric: Boolean
        get() = unitSystem.urlOpenWeatherToken == UnitSystem.METRIC.urlOpenWeatherToken
    val location: String
        get() = locationSystem

    val forecastWeather by lazyDeferred {
        Log.d(TAG,"forecastWeatherDefered")
        forecastRepository.getFutureWeather(LocalDate.now())
    }

    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }

    suspend fun requestRefreshOfData() {
        forecastRepository.requestRefreshOfData()
    }
}
