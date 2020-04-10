package com.bzahov.weatherapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bzahov.weatherapp.data.db.entity.CURRENT_WEATHER_ID
import com.bzahov.weatherapp.data.db.entity.CurrentWeatherEntry
import com.bzahov.weatherapp.data.db.unitlocalized.ImperialCurrentWeatherEntry
import com.bzahov.weatherapp.data.db.unitlocalized.MetricCurrentWeatherEntry

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherEntry: CurrentWeatherEntry)

    //
//    @Query("select * from current_weather where id = $CURRENT_WEATHER_ID")
//    fun getWeatherMetric(): LiveData<MetricCurrentWeatherEntry> //
//
    @Query("select * from current_weather where id = $CURRENT_WEATHER_ID")
    fun getCurrentWeather(): LiveData<CurrentWeatherEntry>

//    @Query("select * from current_weather where id = $CURRENT_WEATHER_ID")
//    fun getWeatherImperial(): LiveData<ImperialCurrentWeatherEntry> //
}