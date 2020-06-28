package com.bzahov.weatherapp.ui.base.viewmodels

import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.internal.enums.UnitSystem
import com.bzahov.weatherapp.internal.lazyDeferred

abstract class FutureWeatherViewModel(
    val forecastRepository: FutureForecastRepository,
    unitProvider: UnitProvider,
    private val internetProvider: InternetProvider,
    private val locationProvider: LocationProvider
) : ViewModel() {


    private val unitSystem = unitProvider.getUnitSystem()
    private val locationSystem = locationProvider.getLocationString()

    val isMetric: Boolean
        get() = unitSystem.urlOpenWeatherToken == UnitSystem.METRIC.urlOpenWeatherToken
    val location: String
        get() = locationSystem

    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }

    fun getTimeZoneOffsetInSeconds() = locationProvider.offsetDateTime

    suspend fun requestRefreshOfData() {
        forecastRepository.requireRefreshOfData = true
        forecastRepository.requestRefreshOfData()
    }

    fun isOnline(): Boolean  = internetProvider.isNetworkConnected
}