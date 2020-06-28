package com.bzahov.weatherapp.ui.base.states

import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R

abstract class AbstractState{
    open val errorString = ForecastApplication.getAppString(R.string.no_data_warning)
    abstract fun calculateData()
}