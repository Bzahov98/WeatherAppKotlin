package com.bzahov.weatherapp.data.db.entity.forecast.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherDetails(
    @PrimaryKey
    val detailsID : Int, // id
    val futureDetailsID : Int, // parent id FutureDayData

    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)