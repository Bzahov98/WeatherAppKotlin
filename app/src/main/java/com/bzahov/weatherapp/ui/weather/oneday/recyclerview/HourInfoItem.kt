package com.bzahov.weatherapp.ui.weather.oneday.recyclerview

import android.animation.ObjectAnimator
import android.view.View
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppResources
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.db.entity.forecast.model.Wind
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.convertDoubleValueAndAbbreviationToString
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.dateTimestampToTimeString
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateIcon
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateWindShort
import com.bzahov.weatherapp.internal.enums.Precipitations
import com.bzahov.weatherapp.internal.enums.WindDirections
import com.bzahov.weatherapp.internal.hide
import com.bzahov.weatherapp.internal.show
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.item_per_three_hours.view.*

public data class HourInfoItem(
	var weatherEntry: FutureDayData,
	val isMetric: Boolean = true,
	val timeZoneOffsetInSeconds: Int = 0,
	val emptyData: Boolean = false
) : Item<OneDayViewHolder>() {

	private var animator: ObjectAnimator? = null

	constructor() : this(FutureDayData(), emptyData = true)

	override fun getLayout() = R.layout.item_per_three_hours

	override fun bind(viewHolder: OneDayViewHolder, position: Int) {
		viewHolder.apply {
			val view = viewHolder.itemView
			updateViewData(view)
		}
	}

	private fun updateViewData(view: View) {
//		animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.4f)
//		if (animator == null) {
//			animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.4f)
//		}
		if (!emptyData) {
			showAllViews(view)
			updateHourText(view, weatherEntry)
			updateIcon(weatherEntry.weatherDetails.last().icon, view.perThreeHoursIcon)
			updateWindUI(weatherEntry.wind, view)
			updateTemperature(view, weatherEntry)
			updatePrecipitation(view)
		} else {
			hideAllViews(view)
		}
	}

	private fun showAllViews(view: View) {
		// TODO Find way to destroy animation when data is available
		animator?.cancel()
		view.animate()?.cancel()
		view.animation?.cancel()
		view.clearAnimation()
		view.perThreeHoursDataView.show()
		view.perThreeHoursTemperature.show()
		view.perThreeHoursPrepInfo.show()
		view.perThreeHoursHourInfo.show()
	}

	private fun hideAllViews(view: View) {
		view.perThreeHoursDataView.hide()
		view.perThreeHoursTemperature.hide()
		view.perThreeHoursPrepInfo.hide()
		view.perThreeHoursHourInfo.hide()

		animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.4f)
		animator?.repeatMode = ObjectAnimator.REVERSE
		animator?.repeatCount = ObjectAnimator.INFINITE
		animator?.duration = getAnimationDuration()
		animator?.start()

	}

	private fun getAnimationDuration() =
		getAppResources()!!.getInteger(R.integer.anim_recycler_empty_duration).toLong()

	private fun updateWindUI(wind: Wind, view: View) {
		val windDirection = WindDirections.getAllWindDirectionByDouble(wind.deg)
		view.perThreeHoursWindIcon.setImageResource(windDirection.image)
		updateWindShort(wind, isMetric, view.perThreeHoursWindSpeedInfo)
	}

	private fun updateHourText(view: View, dayData: FutureDayData) {
		view.perThreeHoursHourInfo.text =
			dateTimestampToTimeString(dayData.dt, timeZoneOffsetInSeconds)
	}

	private fun updateTemperature(view: View, dayData: FutureDayData) {
		val unitAbbreviation = chooseLocalizedUnitAbbreviation(
			isMetric,
			getAppString(R.string.metric_temperature),
			getAppString(R.string.imperial_temperature)
		)
		view.perThreeHoursTemperature.text =
			convertDoubleValueAndAbbreviationToString(dayData.main.temp, unitAbbreviation)
	}

	private fun updatePrecipitation(view: View) {
		val precipitations = PrecipitationData(weatherEntry)

		view.perThreeHoursPrepInfo.text = precipitations.rainPrecipitationText
		view.perThreeHoursPrepIcon.setImageResource(precipitations.precipitationsInfo.image)
	}

	private class PrecipitationData(val weatherEntry: FutureDayData) {

		init {
			calculatePrecipitation()
		}

		lateinit var rainPrecipitationText: String
		lateinit var precipitationsInfo: Precipitations

		private fun calculatePrecipitation() {

			// always in mm
			val unitAbbreviation = getAppString(R.string.metric_precipitation)
			val snowVolume3h = weatherEntry.snow?.precipitationsForLast3hours
			val rainVolume3h = weatherEntry.rain?.precipitationsForLast3hours

			if (snowVolume3h ?: 0.0 > 0 && rainVolume3h ?: 0.0 > 0) {
				rainPrecipitationText =
					Precipitations.RAIN.shortName +
							" $rainVolume3h $unitAbbreviation, " +
							Precipitations.SNOW.shortName +
							": $snowVolume3h"
				precipitationsInfo = Precipitations.SLEET

			} else {
				rainPrecipitationText = Precipitations.NONE.shortName
				precipitationsInfo = Precipitations.NONE
			}

			if (rainVolume3h ?: 0.0 > 0) {
				rainPrecipitationText =
					"$rainVolume3h $unitAbbreviation"

				precipitationsInfo = Precipitations.RAIN
			}
			if (snowVolume3h ?: 0.0 > 0) {
				rainPrecipitationText =
					" $snowVolume3h $unitAbbreviation"

				precipitationsInfo = Precipitations.SNOW
			}


		}
	}


}