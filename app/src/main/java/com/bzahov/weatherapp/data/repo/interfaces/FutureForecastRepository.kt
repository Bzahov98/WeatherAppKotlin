package com.bzahov.weatherapp.data.repo.interfaces

import androidx.lifecycle.LiveData
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import java.time.LocalDate

interface FutureForecastRepository : Repository{
    suspend fun getFutureWeather(
        today: LocalDate,
        isMetric: Boolean
    ): LiveData<List<out FutureDayData>>
}