package com.bzahov.weatherapp.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.internal.enums.UnitSystem

class SettingsFragmentViewModel(
    private val currentForecastRepository: CurrentForecastRepository,
    private val futureForecastRepository: FutureForecastRepository,
    private val unitProvider: UnitProvider
):ViewModel(){
    private val unitSystem = unitProvider.getUnitSystem()

    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    fun notifyForUnitSystemChanged(){
        unitProvider.notifyUnitSystemChanged()
    }

    suspend fun requestRefreshOfData(){
        Log.d("SettingsFragmentViewModel","requestRefreshOfData")
        currentForecastRepository.requestRefreshOfData()
        futureForecastRepository.requestRefreshOfData()
    }
}