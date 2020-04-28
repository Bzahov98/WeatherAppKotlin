package com.bzahov.weatherapp.data.repo

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.dao.ForecastDao
import com.bzahov.weatherapp.data.db.dao.WeatherLocationDao
import com.bzahov.weatherapp.data.db.entity.current.WeatherLocation
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.network.intefaces.FutureWeatherNetworkDataSource
import com.bzahov.weatherapp.data.provider.interfaces.LocationProvider
import com.bzahov.weatherapp.data.provider.interfaces.UnitProvider
import com.bzahov.weatherapp.data.repo.interfaces.FutureForecastRepository
import com.bzahov.weatherapp.data.response.future.ForecastWeatherResponse
import com.bzahov.weatherapp.internal.enums.UnitSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZonedDateTime

private val TAG = "FutureRepositoryImpl"

class FutureForecastRepositoryImpl(
    private val forecastDao: ForecastDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val locationProvider: LocationProvider,
    private val unitSystemProvider: UnitProvider,
    private val futureWeatherNetworkDataSource: FutureWeatherNetworkDataSource
) : FutureForecastRepository {
    val requireRefreshOfData = false

    init {
        futureWeatherNetworkDataSource.downloadedFutureWeather.observeForever {
            persistFetchedCurrentWeather(it)
        }
    }

    // Rework maybe remove isMetric it  i can remove that :)
    override suspend fun getFutureWeather(
        today: LocalDate,
        isMetric: Boolean
    ): LiveData<List<out FutureDayData>> {
        Log.d(TAG, "")
        val resultData = withContext(Dispatchers.IO) {
            val unitSystem = UnitSystem.getOpenWeatherUrlToken(isMetric)
            initWeatherData(unitSystem)
            return@withContext forecastDao.getForecastWeather(today)
        }
        //unitSystemProvider.notifyNoNeedToChangeUnitSystem()
        return resultData
    }

    override suspend fun requestRefreshOfData() {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    // REWORK i can remove that and remove unitSystem param:)
    private suspend fun initWeatherData(unitSystem: String) {

        val lastWeatherLocation = weatherLocationDao.getLocation().value
        if (isFetchNeeded(lastWeatherLocation)) {
            fetchFutureWeather(unitSystem)
        } else {
            Log.e(TAG, "don't fetch data from api")
        } // don't fetch data
    }

    private suspend fun isFetchNeeded(lastWeatherLocation: WeatherLocation?): Boolean {
        val thirtyMinAgo =
            ZonedDateTime.now().minusMinutes(1)//30

        return (lastWeatherLocation == null
                || locationProvider.hasLocationChanged(lastWeatherLocation)
                || lastWeatherLocation.zonedDateTime.isBefore(thirtyMinAgo)
                //|| unitSystemProvider.hasUnitSystemChanged()
                || requireRefreshOfData
                )
    }

    private suspend fun fetchFutureWeather(unitSystem: String) {
        val lastPhysicalLocation = locationProvider.getLastPhysicalDeviceLocation()//WeatherLocation(City(34.2,32.12,"DebugCity",""))//weatherLocationDao.getLocation().value
        if (lastPhysicalLocation == null || !locationProvider.isDeviceLocationSelected()) {
            val location = locationProvider.getLocationString()
            Log.d(TAG,"FetchFutureWeather with LocationString $location and unit $unitSystem")

            futureWeatherNetworkDataSource.fetchForecastWeather(
                location,
                unitSystem// change to unitProvider.getUnitProvide...()
            )
        } else {
            Log.d(TAG,"FetchFutureWeather with LocationString with lat: ${lastPhysicalLocation.latitude} and lon: ${lastPhysicalLocation.longitude} and unit $unitSystem")
            futureWeatherNetworkDataSource.fetchForecastWeatherWithCoords(
                lastPhysicalLocation.latitude,
                lastPhysicalLocation.longitude,
                unitSystem// change to unitProvider.getUnitProvide...()
            )
        }
    }

    private fun persistFetchedCurrentWeather(fetchedFutureWeather: ForecastWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "FetchedData: $fetchedFutureWeather")
                forecastDao.insert(fetchedFutureWeather.list)
                //weatherLocationDao.upsert(fetchedFutureWeather.city.name)
            } catch (e: SQLiteException) {
                Log.e(TAG, "$e")
            }
        }
    }
}