package com.bzahov.weatherapp.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bzahov.weatherapp.data.db.DateConverters
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayDataAndAllWeatherDetails
import java.time.LocalDate

@Dao
@TypeConverters(DateConverters::class)
interface ForecastDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries: List<FutureDayData>)
    @Query("select * from forecast_day where date(dtTxt) >= date(:startDate) ")
    fun getForecastWeather(startDate: LocalDate): LiveData<List<FutureDayData>>

    @Query("select count(futureID) from forecast_day where date(dtTxt) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    @Query("delete from forecast_day where date(dtTxt) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)

    @Transaction
    @Query("SELECT * FROM forecast_day")
    fun getDogsAndOwners(): List<FutureDayDataAndAllWeatherDetails>
}