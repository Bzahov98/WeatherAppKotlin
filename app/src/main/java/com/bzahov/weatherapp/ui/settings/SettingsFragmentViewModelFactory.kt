package com.bzahov.weatherapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository

class SettingsFragmentViewModelFactory (
    private val currentForecastRepository: CurrentForecastRepository,
    private val futureForecastRepository: FutureForecastRepository,
    private val unitProvider: UnitProvider
) : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsFragmentViewModel(currentForecastRepository, futureForecastRepository, unitProvider) as T
    }
}