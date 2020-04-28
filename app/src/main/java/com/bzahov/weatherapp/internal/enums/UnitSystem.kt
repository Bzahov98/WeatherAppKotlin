package com.bzahov.weatherapp.internal.enums

enum class UnitSystem(val urlToken: String, val urlOpenWeatherToken: String) {
    METRIC("m", "metric"), IMPERIAL("f", "imperial");

    companion object {
        fun getUrlToken(isMetric: Boolean) =
            if (isMetric) METRIC.urlToken else IMPERIAL.urlToken

        fun getOpenWeatherUrlToken(metric: Boolean) =
            if (metric) METRIC.urlOpenWeatherToken else IMPERIAL.urlOpenWeatherToken
    }
}