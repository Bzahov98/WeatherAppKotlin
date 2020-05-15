package com.bzahov.weatherapp.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bzahov.weatherapp.data.db.DateConverters
import com.bzahov.weatherapp.data.db.LocalDateConverter
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
@TypeConverters(*[DateConverters::class, LocalDateConverter::class])
interface ForecastDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries: List<FutureDayData>)

    @Query("select * from forecast_day where dtTxt = :date")
    fun getDetailedWeatherByDateString(date: String): LiveData<FutureDayData>

    @Query("select * from forecast_day where date(dtTxt) = date(:date)")
    fun getDetailedWeatherByDate(date: LocalDateTime): LiveData<FutureDayData>

    @Query("select * from forecast_day where date(dtTxt) >= date(:startDate) ")
    fun getForecastWeather(startDate: LocalDate): LiveData<List<FutureDayData>>

    @Query("select count(futureID) from forecast_day where date(dtTxt) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    @Query("delete from forecast_day where date(dtTxt) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)

    @Query("select * from forecast_day where date(dtTxt) BETWEEN date(:startDate) AND date(:endDate)")

    fun getDetailedWeatherByStartEndDate(startDate: LocalDateTime, endDate: LocalDateTime): LiveData<List<FutureDayData>>

//    @Transaction
//    @Query("SELECT * FROM forecast_day where date(dtTxt) < date(:firstDateToKeep)")
//    fun futureDayDataAndAllWeatherDetails(firstDateToKeep: LocalDate): LiveData<List<FutureDayAndWeatherDetails> >
}