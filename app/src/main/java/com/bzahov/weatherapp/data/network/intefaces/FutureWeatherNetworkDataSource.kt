package com.bzahov.weatherapp.data.network.intefaces

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.entity.forecast.model.City
import com.bzahov.weatherapp.data.response.future.ForecastWeatherResponse

interface FutureWeatherNetworkDataSource {
    val downloadedFutureWeather: LiveData<ForecastWeatherResponse>
    suspend fun fetchForecastWeather(
        location: String,
        unit: String
    )

    suspend fun fetchForecastWeatherWithLocation(
        locationCity: City,
        unit: String
    )

    suspend fun fetchForecastWeatherWithCoords(
        lat : Double,
        lon : Double,
        unit: String
    )
}