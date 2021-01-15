package com.bzahov.weatherapp.ui.weather.future.list

import android.util.Log
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.ui.base.states.AbstractWeatherState
import com.bzahov.weatherapp.ui.weather.future.list.recyclerview.FutureWeatherItem
import com.bzahov.weatherapp.ui.weather.oneday.recyclerview.HourInfoItem

data class FutureListState(
	override val weatherData: List<FutureDayData>,
	override val isMetric: Boolean
) : AbstractWeatherState(weatherData, isMetric) {
	lateinit var weatherItems: List<FutureWeatherItem>
	lateinit var hourInfoItemsList: List<HourInfoItem>

	init {
		initEmptyWeatherItem()
		initData()
	}

	override fun setDefaultErrorData() {
		Log.e("FutureListState", "weatherItems is empty or null")
		initEmptyWeatherItem()
		//weatherData.calculateWeatherItems()
		return
	}

	override fun calculateData() {
		weatherData.calculateWeatherItems()
		convertToHourInfoItems()
	}


	private fun List<FutureDayData>.calculateWeatherItems() {
		if(weatherItems.isNullOrEmpty()){
			initEmptyWeatherItem()
			return
		}
		weatherItems =
			this.filter { it.dtTxt.contains(getAppString(R.string.default_future_time_calibration)) }
				.map { FutureWeatherItem(it, isMetric) }
				.apply { }
	}

	private fun initEmptyWeatherItem() {
		hourInfoItemsList = emptyList()
		weatherItems = listOf(
			FutureWeatherItem(),
			FutureWeatherItem(),
			FutureWeatherItem(),
			FutureWeatherItem(),
			FutureWeatherItem()
		)
	}
	private fun convertToHourInfoItems() {
		hourInfoItemsList = weatherData.map {
			HourInfoItem(it, isMetric, timeZoneOffsetInSeconds)
		}.apply { }
	}

	//	val unitAbbreviation: String = UIConverterFieldUtils.getTemperatureUnitAbbreviation(isMetric)
	fun getUnitAbbreviation() = UIConverterFieldUtils.getTemperatureUnitAbbreviation(isMetric)

}