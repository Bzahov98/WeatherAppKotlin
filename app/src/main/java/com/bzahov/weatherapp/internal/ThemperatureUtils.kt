package com.bzahov.weatherapp.internal

class ThemperatureUtils{
    companion object{
        fun convertFahrenheitsToCelsius(fahrenheit: Double): Double {
            return ( (fahrenheit  -  32  ) *  5)/9
        }
    }
}