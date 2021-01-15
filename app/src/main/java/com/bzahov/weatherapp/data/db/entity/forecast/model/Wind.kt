package com.bzahov.weatherapp.data.db.entity.forecast.model


data class Wind(
    val deg: Double,
    val speed: Double
) {
	constructor() : this(-1.0,-1.0)
}