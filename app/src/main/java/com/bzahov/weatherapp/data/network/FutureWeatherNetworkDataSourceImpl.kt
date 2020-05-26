package com.bzahov.weatherapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bzahov.weatherapp.data.db.entity.forecast.model.City
import com.bzahov.weatherapp.data.network.intefaces.FutureWeatherNetworkDataSource
import com.bzahov.weatherapp.data.response.future.ForecastWeatherResponse
import com.bzahov.weatherapp.data.services.OpenWeatherApiService
import com.bzahov.weatherapp.internal.exceptions.NoConnectivityException

class FutureWeatherNetworkDataSourceImpl(
    private val weatherApiService: OpenWeatherApiService
) : FutureWeatherNetworkDataSource {
    private val downloadedFutureWeatherMutable = MutableLiveData<ForecastWeatherResponse>()
    override val downloadedFutureWeather: LiveData<ForecastWeatherResponse>
        get() = downloadedFutureWeatherMutable
private val TAG = "TAG_fetchForecastWeather"

    override suspend fun fetchForecastWeather(location: String, unit: String) {
        try {
            val fetchedForecastWeather = weatherApiService
                .getForecastWeatherAsync(location,null,null, unit)
                .await()
            downloadedFutureWeatherMutable.postValue(fetchedForecastWeather)

            Log.d(TAG, "fetchForecastWeather ( $location, null, null,  $unit)\nResponse: $fetchedForecastWeather \n")
        } catch (ignored: NoConnectivityException) {
            Log.e(TAG, "No Internet Connection:")
        }
    }

    override suspend fun fetchForecastWeatherWithCoords(lat: Double, lon: Double, unit: String) {
        try {
            val fetchedForecastWeather = weatherApiService
                .getForecastWeatherAsync(null,lat,lon, unit)
                .await()

            downloadedFutureWeatherMutable.postValue(fetchedForecastWeather)

            Log.d(TAG, "fetchForecastWeatherWithCoords ( null, $lat, $lon,  $unit)  response: $fetchedForecastWeather \n")
        } catch (ignored: NoConnectivityException) {
            Log.e(TAG, "No Internet Connection:")
        }
    }


    override suspend fun fetchForecastWeatherWithLocation(locationCity: City, unit: String) {
        try {
            val fetchedForecastWeather = weatherApiService
                .getForecastWeatherAsync(null,locationCity.lat,locationCity.lon, unit)
                .await()
            downloadedFutureWeatherMutable.postValue(fetchedForecastWeather)

            Log.d(TAG, "fetchForecastWeatherWithLocation ( null, ${locationCity.lat}, ${locationCity.lon},  $unit)  response: $fetchedForecastWeather \n")
        } catch (ignored: NoConnectivityException) {
            Log.e(TAG, "No Internet Connection:")
        }
    }
}