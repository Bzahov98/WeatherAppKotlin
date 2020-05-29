package com.bzahov.weatherapp.data.repo.interfaces

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry

interface CurrentForecastRepository : Repository {
    suspend fun getCurrentWeather() : LiveData< CurrentWeatherEntry>
}