package com.bzahov.weatherapp.data.repo

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.dao.CurrentWeatherDao
import com.bzahov.weatherapp.data.db.dao.WeatherLocationDao
import com.bzahov.weatherapp.data.db.entity.CurrentWeatherEntry
import com.bzahov.weatherapp.data.db.entity.WeatherLocation
import com.bzahov.weatherapp.data.network.intefaces.WeatherNetworkDataSource
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.response.CurrentWeatherResponse
import com.bzahov.weatherapp.internal.enums.UnitSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime


private const val TAG = "ForecastRepoImpl"

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val locationProvider: LocationProvider,
    private val unitSystemProvider: UnitProvider,
    private val weatherNetworkDataSource: WeatherNetworkDataSource
) : ForecastRepository {
    val requireRefreshOfData = false
    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever {
            persistFetchedCurrentWeather(it)
        }
    }


    override suspend fun getCurrentWeather(
        metric: Boolean,
        location: String
    ): LiveData<out CurrentWeatherEntry> {
        val resultData  = withContext(Dispatchers.IO) {
            val unitSystem = if (metric) UnitSystem.METRIC.urlToken else UnitSystem.IMPERIAL.urlToken // Rework maybe rework it
            initWeatherData(location, unitSystem)
            return@withContext currentWeatherDao.getCurrentWeather()
            /* return@withContext if (metric) currentWeatherDao.getWeatherMetric()
        else currentWeatherDao.getWeatherImperial()*/
        }
        unitSystemProvider.notifyNoNeedToChangeUnitSystem()
        return resultData
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
                weatherLocationDao.upsert(fetchedWeather.location)
            } catch (e: SQLiteException) {
                Log.e(TAG, "$e")
            }
        }
    }

    private suspend fun initWeatherData(location: String, unitSystem: String) {

        val lastWeatherLocation = weatherLocationDao.getLocation().value
        if (isFetchNeeded(lastWeatherLocation) // REWORK lastFetchTime save in base
        ) {
            fetchCurrentWeather(location, unitSystem)
        } else {
            Log.e(TAG, "don't fetch data from api")
        } // don't fetch data
    }

    private fun isFetchNeeded(lastWeatherLocation: WeatherLocation?): Boolean {
        val thirtyMinAgo =
            ZonedDateTime.now().minusMinutes(30)

        return (lastWeatherLocation == null
                || locationProvider.hasLocationChanged(lastWeatherLocation)
                || lastWeatherLocation.zonedDateTime.isBefore(thirtyMinAgo)
                || unitSystemProvider.hasUnitSystemChanged()
                || requireRefreshOfData
                )
    }

    private suspend fun fetchCurrentWeather(location: String, unitSystem: String) {

        //unitSystemProvider.getUnitSystem()
        weatherNetworkDataSource.fetchCurrentWeather(
            location,
            unitSystem
        )
    }

    suspend fun requestRefreshOfData(){
        fetchCurrentWeather(locationProvider.getLocation(),unitSystemProvider.getUnitSystem().urlToken)
    }
}

