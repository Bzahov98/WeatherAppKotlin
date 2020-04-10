package com.bzahov.weatherapp.data.repo

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.entity.CurrentWeatherEntry
import com.bzahov.weatherapp.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean) : LiveData<out CurrentWeatherEntry>
}