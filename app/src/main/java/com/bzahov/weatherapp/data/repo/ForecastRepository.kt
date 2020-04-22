package com.bzahov.weatherapp.data.repo

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.entity.CurrentWeatherEntry
import com.bzahov.weatherapp.data.db.entity.WeatherLocation

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean) : LiveData<out CurrentWeatherEntry>
    suspend fun getWeatherLocation() : LiveData<out WeatherLocation>
}