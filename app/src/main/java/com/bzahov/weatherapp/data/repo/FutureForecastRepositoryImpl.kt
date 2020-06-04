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
import java.time.*

private val TAG = "FutureRepositoryImpl"
const val SECONDS_BETWEEN_REFRESH = 30L

class FutureForecastRepositoryImpl(
    private val forecastDao: ForecastDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val locationProvider: LocationProvider,
    private val unitSystemProvider: UnitProvider,
    private val futureWeatherNetworkDataSource: FutureWeatherNetworkDataSource
) : FutureForecastRepository {

    override var requireRefreshOfData = true

    init {
        futureWeatherNetworkDataSource.downloadedFutureWeather.observeForever {
            persistFetchedFutureWeather(it)
        }
    }

    override suspend fun getFutureWeather(
        today: LocalDate
    ): LiveData<List<FutureDayData>> {
        Log.d(TAG, "getFutureWeather")
        //unitSystemProvider.notifyNoNeedToChangeUnitSystem()
        return withContext(Dispatchers.IO) {
            initWeatherData()
            //REFACTOR: Use 1 to many relation object instead of futureWeather
            //return@withContext forecastDao.futureDayDataAndAllWeatherDetails(today)
            return@withContext forecastDao.getForecastWeather(today)
        }
    }

    override suspend fun getFutureWeatherByDate(
        dateTime: LocalDateTime
    ): LiveData<FutureDayData> {
        Log.d(TAG, "getFutureWeatherByDate")
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext forecastDao.getDetailedWeatherByDate(dateTime)
        }
    }

    override suspend fun getFutureWeatherByDateTimestamp(
        dateStamp: Long
    ): LiveData<FutureDayData> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext forecastDao.getDetailedWeatherByDateTimestamp(dateStamp)
        }
    }

    override suspend fun getFutureWeatherByStartAndEndDate(
        startDate: Long,
        endDate: Long
    ): LiveData<List<FutureDayData>> {
        Log.d(TAG, "getFutureWeatherByStartAndEndDate")
        return withContext(Dispatchers.IO) {
            initWeatherData()
            val detailedWeatherByStartEndDate =
                forecastDao.getDetailedWeatherByStartEndDate(startDate, endDate)
            return@withContext detailedWeatherByStartEndDate
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
        }
    }

    private suspend fun isFetchNeeded(lastWeatherLocation: WeatherLocation?): Boolean {

        val thirtySecAgo =
            ZonedDateTime.now().minusSeconds(SECONDS_BETWEEN_REFRESH)//30
        Log.e(
            TAG, "lastWeatherLocation == null ${lastWeatherLocation == null}" +
                    "|| locationProvider.hasLocationChanged(lastWeatherLocation) ${locationProvider.hasLocationChanged(
                        lastWeatherLocation ?: WeatherLocation()
                    )}" +
                    "|| lastWeatherLocation.zonedDateTime.isBefore(thirtySecAgo) ${lastWeatherLocation?.zonedDateTime?.isBefore(
                        thirtySecAgo
                    )}" +
                    "|| requireRefreshOfData $requireRefreshOfData"
        )
        return (lastWeatherLocation == null
                || locationProvider.hasLocationChanged(lastWeatherLocation)
                || lastWeatherLocation.zonedDateTime.isBefore(thirtySecAgo)
                || requireRefreshOfData
                )
    }

    private suspend fun fetchFutureWeather() {

        val unitSystem = unitSystemProvider.getUnitSystem().urlOpenWeatherToken
        val lastPhysicalLocation =
            locationProvider.getLastPhysicalDeviceLocation()
        if (lastPhysicalLocation == null || !locationProvider.isDeviceLocationSelected()) {
            val location = locationProvider.getLocationString()
            Log.d(TAG, "FetchFutureWeather with LocationString $location and unit $unitSystem")

            futureWeatherNetworkDataSource.fetchForecastWeather(
                location,
                unitSystem
            )
            requireRefreshOfData = false
        } else {
            Log.d(
                TAG,
                "FetchFutureWeather with lat: ${lastPhysicalLocation.latitude} and lon: ${lastPhysicalLocation.longitude} and unit $unitSystem"
            )
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
                deleteOldEntities(fetchedFutureWeather)
                forecastDao.insert(fetchedFutureWeather.list)
                locationProvider.offsetDateTime = fetchedFutureWeather.city.timezone
                //forecastDao.insert(fetchedFutureWeather.list)
                //weatherLocationDao.upsert(fetchedFutureWeather.city.name)
            } catch (e: NullPointerException) {
                locationProvider.resetCustomLocationToDefault()
            } catch (e: SQLiteException) {
                Log.e(TAG, "$e")
            }
        }
    }

    private fun deleteOldEntities(fetchedFutureWeather: ForecastWeatherResponse) {
        val firstDateToKeep = LocalDate.now().minusDays(3).toEpochDay()
        forecastDao.deleteOldEntries(
            LocalDate.ofEpochDay(firstDateToKeep).minusDays(1).atTime(LocalTime.MIN).toEpochSecond(
                ZoneOffset.ofTotalSeconds(locationProvider.offsetDateTime)
            )
        )
    }
}