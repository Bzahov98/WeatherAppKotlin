package com.bzahov.weatherapp.ui.weather.current

import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.ForecastRepository
import com.bzahov.weatherapp.internal.enums.UnitSystem
import com.bzahov.weatherapp.internal.lazyDeferred

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()
    private val locationSystem = locationProvider.getLocation()

    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC
    val location: String
        get() = locationSystem

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric,location)
    }

    val weatherLocation by lazyDeferred{
        forecastRepository.getWeatherLocation()
    }
}
