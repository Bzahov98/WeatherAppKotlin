package com.bzahov.weatherapp.data.repo

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.dao.CurrentWeatherDao
import com.bzahov.weatherapp.data.db.dao.WeatherLocationDao
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation
import com.bzahov.weatherapp.data.network.intefaces.CurrentWeatherNetworkDataSource
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.CurrentForecastRepository
import com.bzahov.weatherapp.data.response.current.CurrentWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime


private const val TAG = "ForecastRepoImpl"

class CurrentForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val locationProvider: LocationProvider,
    private val unitSystemProvider: UnitProvider,
    private val currentWeatherNetworkDataSource: CurrentWeatherNetworkDataSource
) : CurrentForecastRepository {
    override var requireRefreshOfData = false

    init {
        currentWeatherNetworkDataSource.downloadedCurrentWeather.observeForever {
            persistFetchedCurrentWeather(it)
        }
    }


    override suspend fun getCurrentWeather()
            : LiveData<CurrentWeatherEntry> { // REWORK i can remove that :)
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
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
            } catch (e: NullPointerException) {
                locationProvider.resetCustomLocationToDefault()
                requireRefreshOfData = true
            } catch (e: SQLiteException) {
                Log.e(TAG, "$e")
            }
        }
    }

    private suspend fun initWeatherData() {

        val lastWeatherLocation = weatherLocationDao.getLocationNotLive()
        if (isFetchNeeded(lastWeatherLocation)
        ) {
            fetchCurrentWeather()
        } else {
            Log.e(TAG, "don't fetch data from api")
        } // don't fetch data
    }

    private suspend fun isFetchNeeded(lastWeatherLocation: WeatherLocation?): Boolean {
        val thirtyMinAgo =
            ZonedDateTime.now().minusSeconds(SECONDS_BETWEEN_REFRESH)//30

        return (lastWeatherLocation == null
                || locationProvider.hasLocationChanged(lastWeatherLocation)
                || lastWeatherLocation.zonedDateTime.isBefore(thirtyMinAgo)
                //|| unitSystemProvider.hasUnitSystemChanged()
                || requireRefreshOfData
                )
    }

    private suspend fun fetchCurrentWeather() {

        currentWeatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString(),
            unitSystemProvider.getUnitSystem().urlToken
        )
    }

    override suspend fun requestRefreshOfData() {
        fetchCurrentWeather()
    }


}

