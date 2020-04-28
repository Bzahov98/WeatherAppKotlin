package com.bzahov.weatherapp.ui.settings

import androidx.lifecycle.ViewModel
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository

class SettingsFragmentViewModel(
    private val currentForecastRepository: CurrentForecastRepository,
    private val unitProvider: UnitProvider
):ViewModel(){
    private val unitSystem = unitProvider.getUnitSystem()


    fun notifyForUnitSystemChanged(){
        unitProvider.notifyUnitSystemChanged()
    }
}