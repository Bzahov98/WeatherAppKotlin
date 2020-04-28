package com.bzahov.weatherapp.data.network.intefaces

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.response.current.CurrentWeatherResponse

interface CurrentWeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    suspend fun fetchCurrentWeather( //Update
        location: String,
        unit: String
    )
}