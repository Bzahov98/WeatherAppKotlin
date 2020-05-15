package com.bzahov.weatherapp.ui.weather.oneday.recyclerview

import android.view.View
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.glide.GlideApp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_per_three_hours.view.*

data class HourInfoItem(
    val weatherEntry: FutureDayData,
    val isMetric: Boolean
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
        updateIcon(view)
        updateTemperature(view)
    }

    private fun updateHourText(view: View) {
        view.perThreeHoursHourInfo.text = UIConverterFieldUtils.dateTimestampToTimeString(weatherEntry.dt)
    }

    private fun updateIcon(view: View) {
        val iconNumber = weatherEntry.weatherDetails.last().icon
        val iconUrl = UIConverterFieldUtils.getOpenWeatherIconUrl(iconNumber)
        GlideApp.with(view)
            .load(iconUrl)
            .into(view.perThreeHoursIcon)
    }

    private fun updateTemperature(view: View) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            isMetric,
            ForecastApplication.getAppString(R.string.metric_temperature),
            ForecastApplication.getAppString(R.string.imperial_temperature)
        )
        view.perThreeHoursTemperature.text = "${String.format("%.1f", weatherEntry.main.temp)}$unitAbbreviation"
    }


}