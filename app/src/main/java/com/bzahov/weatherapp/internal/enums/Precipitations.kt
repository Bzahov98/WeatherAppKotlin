package com.bzahov.weatherapp.internal.enums

import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R

enum class Precipitations(val shortName:String, val fullDescription : String, val image : Int) {
    SLEET(getAppString(R.string.weather_text_sleet),getAppString(R.string.weather_text_precipitation_sleet),R.drawable.icons8_sleet_60),
    SNOW(getAppString(R.string.weather_text_snow),getAppString(R.string.weather_text_precipitation_snow),R.drawable.icons8_snow_50),
    RAIN(getAppString(R.string.weather_text_rain),getAppString(R.string.weather_text_precipitation_rain),R.drawable.icons8_rainmeter),
    NONE(getAppString(R.string.weather_text_none),getAppString(R.string.weather_text_precipitation_none),R.drawable.icons8_no_rain_50)
}