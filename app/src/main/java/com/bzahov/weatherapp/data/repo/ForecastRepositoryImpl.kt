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


    override suspend fun getCurrentWeather(
        metric: Boolean,
        location: String
    ): LiveData<out CurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
           val unitSystem = if(metric)"m" else "f"
            initWeatherData(location, unitSystem)
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

    private suspend fun initWeatherData(location: String,unitSystem: String) {
        if (
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1)) // REWORK lastFetchTime save in base
            } else { true }
        ) {
            fetchCurrentWeather(location, unitSystem)
        }else{
            Log.e(TAG,"don't fetch data from api")} // don't fetch data
    }

    private suspend fun fetchCurrentWeather(location: String,unitSystem: String) {
       // val app = ForecastApplication// REWORK find better way
       // val unitSystem = app.getAppString(R.string.default_unit_system) //  find better way

        weatherNetworkDataSource.fetchCurrentWeather(
            location,
            unitSystem
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