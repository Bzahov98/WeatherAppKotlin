package com.bzahov.weatherapp.ui.weather.oneday.recyclerview

import android.view.View
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
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_per_three_hours.view.*

data class HourInfoItem(
    val weatherEntry: FutureDayData,
    val isMetric: Boolean,
    val timeZoneOffsetInSeconds: Int = 0
) : Item() {

    override fun getLayout() = R.layout.item_per_three_hours

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            val view = viewHolder.itemView
            updateViewData(view)
        }
    }

    private fun updateViewData(view: View) {
        updateHourText(view)
        updateIcon(weatherEntry.weatherDetails.last().icon, view.perThreeHoursIcon)
        updateWindUI(weatherEntry.wind, view)
        updateTemperature(view)
        updatePrecipitation(view)
    }

    private fun updateWindUI(wind: Wind, view: View) {
        val windDirection = WindDirections.getWindDirectionByDouble(wind.deg)
        view.perThreeHoursWindIcon.setImageResource(windDirection.image)
        updateWindShort(wind, isMetric, view.perThreeHoursWindSpeedInfo)
    }

    private fun updateHourText(view: View) {
        view.perThreeHoursHourInfo.text =
            dateTimestampToTimeString(weatherEntry.dt, timeZoneOffsetInSeconds)
    }

    private fun updateTemperature(view: View) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            isMetric,
            getAppString(R.string.metric_temperature),
            getAppString(R.string.imperial_temperature)
        )
        view.perThreeHoursTemperature.text =
            convertDoubleValueAndAbbreviationToString(weatherEntry.main.temp, unitAbbreviation)
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

        lateinit var rainPrecipitationText   : String
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