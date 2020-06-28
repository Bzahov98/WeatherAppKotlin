package com.bzahov.weatherapp.internal

import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import java.time.LocalDateTime
import java.time.ZoneOffset

class OtherUtils {
    companion object{

        fun isDayTime(timeOffset: Int = 0): (FutureDayData) -> Boolean {
            return {
                // REWORK Fix Offset
                val currentDay = LocalDateTime.ofEpochSecond(it.dt,0, ZoneOffset.ofTotalSeconds(timeOffset))

                currentDay.isAfter(currentDay.withHour(8)).and(
                    currentDay.isBefore(currentDay.withHour(20))
                )
            }
        }
    }
}