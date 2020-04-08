package com.bzahov.weatherapp.data.db.unitlocalized

data class ImperialCurrentWeatherEntry(
// REWORK
    override val temperature: Double,
    override val conditionText: String,
    override val conditionIconUrl: String,
    override val windSpeed: Double,
    override val windDirection: String,
    override val precipitationVolume: Double,
    override val feelsLikeTemperature: Double,
    override val visibilityDistance: Double


) : UnitSpecificCurrentWeatherEntry {

}

/*
    @ColumnInfo(name = "tempC")
    override val temperature: Double,
    @ColumnInfo(name = "condition_text")
    override val conditionText: String,
    @ColumnInfo(name = "condition_icon")
    override val conditionIconUrl: String,
    @ColumnInfo(name = "windKph")
    override val windSpeed: Double,
    @ColumnInfo(name = "windDir")
    override val windDirection: String,
    @ColumnInfo(name = "precipMm")
    override val precipitationVolume: Double,
    @ColumnInfo(name = "feelslikeC")
    override val feelsLikeTemperature: Double,
    @ColumnInfo(name = "visKm")
    override val visibilityDistance: Double
    */