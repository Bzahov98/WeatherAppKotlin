package com.bzahov.weatherapp.data.network.intefaces

import android.view.textclassifier.TextLanguage
import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    suspend fun fetchCurrentWeather( //Update
        location: String,
//        languageCode: String,
        unit: String

    )
}