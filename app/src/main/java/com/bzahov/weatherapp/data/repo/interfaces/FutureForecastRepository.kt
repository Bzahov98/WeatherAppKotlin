package com.bzahov.weatherapp.data.repo.interfaces

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import java.time.LocalDate
import java.time.LocalDateTime

interface FutureForecastRepository : Repository {
    suspend fun getFutureWeather(
        today: LocalDate
    ): LiveData<List<FutureDayData>>

    suspend fun getFutureWeatherByDate(
        dateTime: LocalDateTime
    ): LiveData<FutureDayData>

    suspend fun getFutureWeatherByDateTimestamp(
        dateStamp: Long
    ): LiveData<FutureDayData>

    suspend fun getFutureWeatherByStartAndEndDate(
        startDate: Long,
        endDate: Long
    ): LiveData<List<FutureDayData>>
}