package com.bzahov.weatherapp.ui.settings

import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.ForecastRepository

class SettingsFragmentViewModel(
    private val forecastRepository: ForecastRepository,
    private val unitProvider: UnitProvider
):ViewModel(){
    private val unitSystem = unitProvider.getUnitSystem()


    fun notifyForUnitSystemChanged(){
        unitProvider.notifyUnitSystemChanged()
    }
}