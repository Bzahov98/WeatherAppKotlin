package com.bzahov.weatherapp.ui.anychartGraphs.specificUtils

import com.bzahov.weatherapp.ui.weather.oneday.OneDayWeatherState

interface AbstractDayDataChartUtils {
		fun createChart(
			weatherStateData: OneDayWeatherState?,
			chartType: String = CHART_TYPE_DEFAULT
		)
		companion object{
			const val CHART_TYPE_DEFAULT = "DEFAULT"

		}
}