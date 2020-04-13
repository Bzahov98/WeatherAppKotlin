package com.bzahov.weatherapp.data.repo

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.CurrentWeatherDao
import com.bzahov.weatherapp.data.db.entity.CurrentWeatherEntry
import com.bzahov.weatherapp.data.network.intefaces.WeatherNetworkDataSource
import com.bzahov.weatherapp.data.response.CurrentWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

private const val TAG = "ForecastRepoImpl"
class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource
) : ForecastRepository {

    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever {
            persistFetchedCurrentWeather(it)
        }
    }


    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out /*UnitSpecificCurrentWeatherEntry*/CurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
        }
    }

    private suspend fun initWeatherData() {
        if (
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1)) // REWORK lastFetchTime save in base
            } else { true }
        ) {
            fetchCurrentWeather()
        }else{
            Log.e(TAG,"don't fetch data from api")} // don't fetch data
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            "Sofia",
            "m"
        )
    }


    private fun fetchCurrentWeather(lastFetchData: ZonedDateTime): Boolean {
        val thirtyMinAgo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZonedDateTime.now().minusMinutes(30)
        } else {
            return true
        }
        return true
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinAgo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZonedDateTime.now().minusMinutes(30)
        } else {
            return true
        }
        return lastFetchTime.isBefore(thirtyMinAgo)
    }


}