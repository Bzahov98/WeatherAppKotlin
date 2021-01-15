package com.bzahov.weatherapp.ui.fragments

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.InternetProvider
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.internal.enums.UnitSystem

class SettingsFragmentViewModel(
    private val currentForecastRepository: CurrentForecastRepository,
    private val futureForecastRepository: FutureForecastRepository,
    private val unitProvider: UnitProvider,
    private val locationProvider: LocationProvider,
    private val internetProvider: InternetProvider
):ViewModel(){
    private val unitSystem = unitProvider.getUnitSystem()

    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    suspend fun requestRefreshOfData(){
        Log.d("SettingsFragmentViewModel","requestRefreshOfData")
        currentForecastRepository.requestRefreshOfData()
        futureForecastRepository.requestRefreshOfData()
    }

    fun isLocationEnabled(): Boolean{
        return locationProvider.isLocationEnabled()
    }

    fun isOnline(): Boolean  = internetProvider.isNetworkConnected
}