package com.bzahov.weatherapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bzahov.weatherapp.data.WeatherApiService
import com.bzahov.weatherapp.data.network.intefaces.WeatherNetworkDataSource
import com.bzahov.weatherapp.data.response.CurrentWeatherResponse
import com.bzahov.weatherapp.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService
) : WeatherNetworkDataSource {
    private val downloadedCurrentWeatherMutable = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = downloadedCurrentWeatherMutable
    override suspend fun fetchCurrentWeather(location: String,/*languageCode: String,*/ unit: String) {
        try {
            val fetchedCurrentWeather = weatherApiService
                .getCurrentWeather(location/*,languageCode*/,unit)
                .await()
                downloadedCurrentWeatherMutable.postValue(fetchedCurrentWeather)
        }catch (e: NoConnectivityException){
            Log.e("TagConnectivity","No Internet Connection", e)
        }
    }
}