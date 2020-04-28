package com.bzahov.weatherapp.data.db.entity.forecast.entities

import androidx.room.Embedded
import androidx.room.Relation

class FutureDayDataAndAllWeatherDetails( //OwnerWithDogs
    @Embedded val futureDayData: FutureDayData, // owner
    @Relation(
        parentColumn = "futureID",
        entityColumn = "futureDetailsID"
        )
    val weatherDetailsList: List<WeatherDetails> // list<Dog
)