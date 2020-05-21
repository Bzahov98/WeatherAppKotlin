package com.bzahov.weatherapp.data.repo.interfaces

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import java.time.LocalDate
import java.time.LocalDateTime

interface FutureForecastRepository : Repository {
    suspend fun getFutureWeather(
        today: LocalDate,
        isMetric: Boolean
    ): LiveData<List<out FutureDayData>>

    suspend fun getFutureWeatherByDate(
        dateTime: LocalDateTime,
        isMetric: Boolean
    ): LiveData<out FutureDayData>

    suspend fun getFutureWeatherByDateTimestamp(
        dateStamp: Long,
        isMetric: Boolean
    ): LiveData<out FutureDayData>

    suspend fun getFutureWeatherByStartAndEndDate(
        startDate: Long,
        endDate: Long
    ): LiveData<List<out FutureDayData>>
}