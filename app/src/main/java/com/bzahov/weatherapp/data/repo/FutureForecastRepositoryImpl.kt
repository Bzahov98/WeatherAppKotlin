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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

private val TAG = "FutureRepositoryImpl"

class FutureForecastRepositoryImpl(
    private val forecastDao: ForecastDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val locationProvider: LocationProvider,
    private val unitSystemProvider: UnitProvider,
    private val futureWeatherNetworkDataSource: FutureWeatherNetworkDataSource
) : FutureForecastRepository {
    val requireRefreshOfData = true

    init {
        futureWeatherNetworkDataSource.downloadedFutureWeather.observeForever{
            persistFetchedFutureWeather(it)
        }
    }

    // REWORK maybe remove isMetric it  i can remove that :)
    override suspend fun getFutureWeather(
        today: LocalDate,
        isMetric: Boolean
    ): LiveData<List<out FutureDayData>> {
        Log.d(TAG, "")
        //unitSystemProvider.notifyNoNeedToChangeUnitSystem()
        return withContext(Dispatchers.IO) {
            initWeatherData()
            //REFACTOR: Use 1 to many relation object instead of futureWeather
            //return@withContext forecastDao.futureDayDataAndAllWeatherDetails(today)
            return@withContext forecastDao.getForecastWeather(today)
        }
    }

    override suspend fun getFutureWeatherByDate(
        dateTime: LocalDateTime,
        isMetric: Boolean
    ): LiveData<out FutureDayData> {
        Log.d(TAG, "")
        return withContext(Dispatchers.IO){
            initWeatherData()
            return@withContext forecastDao.getDetailedWeatherByDate(dateTime)
        }
    }


    override suspend fun requestRefreshOfData() {
        fetchFutureWeather()
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }




    private suspend fun initWeatherData() {

        val lastWeatherLocation = weatherLocationDao.getLocationNotLive()
        if (isFetchNeeded(lastWeatherLocation)) {
            fetchFutureWeather()
        } else {
            Log.e(TAG, "don't fetch data from api")
        } // don't fetch data
    }

    private suspend fun isFetchNeeded(lastWeatherLocation: WeatherLocation?): Boolean {
        val thirtyMinAgo =
            ZonedDateTime.now().minusMinutes(0)//30
        Log.e(TAG,"lastWeatherLocation == null ${lastWeatherLocation == null}" +
                "|| locationProvider.hasLocationChanged(lastWeatherLocation) ${locationProvider.hasLocationChanged(lastWeatherLocation?:WeatherLocation())}" +
                "|| lastWeatherLocation.zonedDateTime.isBefore(thirtyMinAgo) ${lastWeatherLocation?.zonedDateTime?.isBefore(thirtyMinAgo)}" +
                "|| unitSystemProvider.hasUnitSystemChanged() ${unitSystemProvider.hasUnitSystemChanged()}" +
                "|| requireRefreshOfData $requireRefreshOfData")
        return (lastWeatherLocation == null
                || locationProvider.hasLocationChanged(lastWeatherLocation)
                || lastWeatherLocation.zonedDateTime.isBefore(thirtyMinAgo)
                || unitSystemProvider.hasUnitSystemChanged()
                || requireRefreshOfData
                )
    }

    private suspend fun fetchFutureWeather() {

        val unitSystem = unitSystemProvider.getUnitSystem().urlOpenWeatherToken
        val lastPhysicalLocation = locationProvider.getLastPhysicalDeviceLocation()//WeatherLocation(City(34.2,32.12,"DebugCity",""))//weatherLocationDao.getLocation().value

        if (lastPhysicalLocation == null || !locationProvider.isDeviceLocationSelected()) {
            val location = locationProvider.getLocationString()
            Log.d(TAG,"FetchFutureWeather with LocationString $location and unit $unitSystem")

            futureWeatherNetworkDataSource.fetchForecastWeather(
                location,
                unitSystem
            )
        } else {
            Log.d(TAG,"FetchFutureWeather with lat: ${lastPhysicalLocation.latitude} and lon: ${lastPhysicalLocation.longitude} and unit $unitSystem")
            futureWeatherNetworkDataSource.fetchForecastWeatherWithCoords(
                lastPhysicalLocation.latitude,
                lastPhysicalLocation.longitude,
                unitSystem
            )
        }
    }

    private fun persistFetchedFutureWeather(fetchedFutureWeather: ForecastWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "FetchedData: $fetchedFutureWeather")
                forecastDao.insert(fetchedFutureWeather.list)
                //forecastDao.insert(fetchedFutureWeather.list)
                //weatherLocationDao.upsert(fetchedFutureWeather.city.name)
            } catch (e: SQLiteException) {
                Log.e(TAG, "$e")
            }
        }
    }
}