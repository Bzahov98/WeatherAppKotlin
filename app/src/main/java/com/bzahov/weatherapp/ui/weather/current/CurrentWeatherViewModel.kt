package com.bzahov.weatherapp.ui.weather.current

import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.internal.enums.UnitSystem
import com.bzahov.weatherapp.internal.lazyDeferred

class CurrentWeatherViewModel(
    private val currentForecastRepository: CurrentForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()

    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weather by lazyDeferred {
        currentForecastRepository.getCurrentWeather()
    }

    val weatherLocation by lazyDeferred{
        currentForecastRepository.getWeatherLocation()
    }
    suspend fun requestRefreshOfData(){

        currentForecastRepository.requestRefreshOfData()
    }
}
