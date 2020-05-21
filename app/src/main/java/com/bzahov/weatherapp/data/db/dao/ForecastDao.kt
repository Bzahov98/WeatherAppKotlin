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

    @Query("select * from forecast_day where date(dtTxt) = date(:dateTime)")
    fun getDetailedWeatherByDate(dateTime: LocalDateTime): LiveData<FutureDayData>

    @Query("select * from forecast_day where dt = :dateStamp")
    fun getDetailedWeatherByDateTimestamp(dateStamp: Long): LiveData<FutureDayData>

    @Query("select * from forecast_day where date(dtTxt) >= date(:startDate) ")
    fun getForecastWeather(startDate: LocalDate): LiveData<List<FutureDayData>>

    @Query("select count(futureID) from forecast_day where date(dtTxt) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    @Query("delete from forecast_day where dt < :firstDateToKeep")
    fun deleteOldEntries(firstDateToKeep: Long)

    @Query("select * from forecast_day where dt  BETWEEN :startDate AND :endDate")
    fun getDetailedWeatherByStartEndDate(startDate: Long, endDate: Long): LiveData<List<FutureDayData>>

//    @Transaction
//    @Query("SELECT * FROM forecast_day where date(dtTxt) < date(:firstDateToKeep)")
//    fun futureDayDataAndAllWeatherDetails(firstDateToKeep: LocalDate): LiveData<List<FutureDayAndWeatherDetails> >
}