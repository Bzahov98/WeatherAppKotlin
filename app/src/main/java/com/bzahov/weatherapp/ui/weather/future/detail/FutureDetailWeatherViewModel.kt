package com.bzahov.weatherapp.ui.weather.future.detail

import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.internal.lazyDeferred
import com.bzahov.weatherapp.ui.base.FutureWeatherViewModel
import java.time.LocalDateTime

class FutureDetailWeatherViewModel(
    detailDate: LocalDateTime,
    forecastRepository: FutureForecastRepository,
    unitProvider: UnitProvider,
    locationProvider: LocationProvider
) : FutureWeatherViewModel(forecastRepository,unitProvider,locationProvider) {
    val weather by lazyDeferred {
        forecastRepository.getFutureWeatherByDate(detailDate, super.isMetric)
    }
}
