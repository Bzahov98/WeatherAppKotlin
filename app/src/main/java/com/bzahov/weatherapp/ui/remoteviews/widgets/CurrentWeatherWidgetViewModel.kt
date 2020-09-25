package com.bzahov.weatherapp.ui.remoteviews.widgets

import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.internal.lazyDeferred

class CurrentWeatherWidgetViewModel(
    private val currentForecastRepository: CurrentForecastRepository,
    unitProvider: UnitProvider,
    val locationProvider: LocationProvider,
    val internetProvider: InternetProvider
) : ViewModel() {

    val weatherLocation by lazyDeferred {
        currentForecastRepository.getWeatherLocation()
    }

    suspend fun requestRefreshOfData() {

        currentForecastRepository.requestRefreshOfData()
    }
}