package com.bzahov.weatherapp.data.repo.interfaces

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation

interface Repository {
    suspend fun requestRefreshOfData()
    suspend fun getWeatherLocation() : LiveData<out WeatherLocation>
}