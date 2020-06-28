package com.bzahov.weatherapp.ui.weather.future.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import java.time.LocalDateTime

class FutureDetailWeatherViewModelFactory(
    private val detailDate: LocalDateTime,
    private val forecastRepository: FutureForecastRepository,
    private val unitProvider: UnitProvider,
    private val locationProvider: LocationProvider,
    private val internetProvider: InternetProvider
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FutureDetailWeatherViewModel(
            detailDate,
            forecastRepository,
            unitProvider,
            locationProvider,
            internetProvider
        ) as T
    }
}