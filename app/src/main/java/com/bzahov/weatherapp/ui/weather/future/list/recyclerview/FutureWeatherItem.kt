package com.bzahov.weatherapp.ui.weather.future.list.recyclerview

import android.view.View
import android.widget.TextView
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.convertDoubleValueAndAbbreviationToString
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateIcon
import com.bzahov.weatherapp.internal.hide
import com.bzahov.weatherapp.internal.show
import com.bzahov.weatherapp.ui.animationUtils.ObjectAnimatorUtils
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.item_future_weather.view.*
import kotlinx.android.synthetic.main.layout_temperature_view.view.*

class FutureWeatherItem(
	val weatherEntry: FutureDayData,
	val isMetric: Boolean = true,
	val timeZoneOffsetInSeconds: Int = 0,
	val emptyData: Boolean = false

) : Item<FutureViewHolder>() {
	private val TAG = "FutureWeatherItem"

	constructor() : this(FutureDayData(true), emptyData = true)

	override fun getLayout() = R.layout.item_future_weather

	override fun bind(viewHolder: FutureViewHolder, position: Int) {
		viewHolder.apply {
			val view = viewHolder.itemView
			updateViewData(view)
		}
	}

	private fun updateViewData(view: View) {

		if (!emptyData) {
			showAllViews(view)
			updateDate(view)
			updateTemperature(view)
			updateCondition(view)
			updateIcon(weatherEntry.weatherDetails.last().icon, view.futureConditionIcon)
		} else {
			hideAllViews(view)
		}
	}

	private fun showAllViews(view: View) {

		ObjectAnimatorUtils.setAlphaToViewToNormal(view)
		view.isClickable = true
		view.futureItemLayout?.show()
		view.futureWeatherDate.show()
		view.futureConditionTextHolder.show()
		view.futureConditionText.show()
		view.futureConditionIcon.show()
		view.futureTemperatureView.show()
//
	}


	private fun hideAllViews(view: View) {
		view.isClickable = false
		//view.futureItemLayout?.hide()
		view.futureWeatherDate.hide()
		view.futureConditionTextHolder.hide()
		view.futureConditionText.hide()
		view.futureConditionIcon.hide()
		view.futureTemperatureView.hide()

		ObjectAnimatorUtils.startFadeAnimationToView(view)

	}

	private fun updateCondition(view: View) {
		(view.futureConditionText as TextView).text =
			UIConverterFieldUtils.getAllDescriptionsString(weatherEntry.weatherDetails)
	}

	private fun updateTemperature(view: View) {
		val temp = weatherEntry.main.temp
		val tempFeelsLike = weatherEntry.main.feelsLike
		val tempMin = weatherEntry.main.tempMin
		val tempMax = weatherEntry.main.tempMax

		val unitAbbreviation = chooseLocalizedUnitAbbreviation(
			isMetric,
			getAppString(R.string.metric_temperature),
			getAppString(R.string.imperial_temperature)
		)
		view.tempViewTemperature.text =
			convertDoubleValueAndAbbreviationToString(temp, unitAbbreviation)
		view.tempViewFeelsLike.text =
			convertDoubleValueAndAbbreviationToString(tempFeelsLike, unitAbbreviation)
		// TODO: commented ForDebug to see all attributes
//        if (tempMax == temp && tempMin == temp) {
//            view.futureMinMaxGroup.visibility = View.GONE
//        } else {
//            view.futureMinMaxGroup.visibility = View.VISIBLE
		view.tempViewMinTemp.text =
			convertDoubleValueAndAbbreviationToString(tempMin, unitAbbreviation)
		view.tempViewMaxTemp.text =
			convertDoubleValueAndAbbreviationToString(tempMax, unitAbbreviation)
//        }
	}

	private fun updateDate(view: View) {
		(view.futureWeatherDate as TextView).text =
			UIConverterFieldUtils.dateTimestampToDateTimeString(
				weatherEntry.dt,
				timeZoneOffsetInSeconds
			)
	}
}


