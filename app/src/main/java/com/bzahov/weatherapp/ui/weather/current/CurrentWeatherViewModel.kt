package com.resocoder.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel;
import com.bzahov.weatherapp.data.repo.ForecastRepository
import com.bzahov.weatherapp.internal.UnitSystem
import com.bzahov.weatherapp.internal.lazyDeferred

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {

    private val unitSystem = UnitSystem.METRIC // REWORK get form settings later

    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC    // fromSettings

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }
}
