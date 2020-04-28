package com.bzahov.weatherapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bzahov.weatherapp.data.WeatherApiService
import com.bzahov.weatherapp.data.network.intefaces.CurrentWeatherNetworkDataSource
import com.bzahov.weatherapp.data.response.current.CurrentWeatherResponse
import com.bzahov.weatherapp.internal.exceptions.NoConnectivityException

class CurrentWeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService
) : CurrentWeatherNetworkDataSource {
        private val downloadedCurrentWeatherMutable = MutableLiveData<CurrentWeatherResponse>()
        override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = downloadedCurrentWeatherMutable
    override suspend fun fetchCurrentWeather(location: String, unit: String) {
        try {
            val fetchedCurrentWeather = weatherApiService
                .getCurrentWeatherAsync(location,unit)
                .await()
                downloadedCurrentWeatherMutable.postValue(fetchedCurrentWeather)

            Log.d("TAG_connectivity", "$fetchedCurrentWeather")
        }catch (ignored: NoConnectivityException){
            Log.e("TagConnectivity","No Internet Connection:")
        }
    }
}