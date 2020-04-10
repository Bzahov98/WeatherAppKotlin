package com.bzahov.weatherapp.data.repo

import android.os.Build
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
            /* return@withContext if (metric) currentWeatherDao.getWeatherMetric()
        else currentWeatherDao.getWeatherImperial()*/
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
        }else{} // dont fetch data
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            "Sofia",
            /*Locale.getDefault().language,*/
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
            return true // TODO hh
        }
        return lastFetchTime.isBefore(thirtyMinAgo)
    }


}