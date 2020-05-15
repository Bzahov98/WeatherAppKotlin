package com.bzahov.weatherapp.ui.weather.future.list.recyclerview

import android.view.View
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.glide.GlideApp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_future_weather.view.*

class FutureWeatherItem(
    val weatherEntry: FutureDayData,
    val isMetric: Boolean
) : Item() {
    override fun getLayout() = R.layout.item_future_weather
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            val view = viewHolder.itemView
            updateViewData(view)
        }
    }

    private fun updateViewData(view: View) {
        updateDate(view)
        updateTemperature(view)
        updateCondition(view)
        updateIcon(view)
    }

    private fun updateCondition(view: View) {
        view.futureConditionText.text = UIConverterFieldUtils.getAllDescriptionsString(weatherEntry.weatherDetails)
    }



    private fun updateIcon(view: View) {
        val iconNumber = weatherEntry.weatherDetails.last().icon
        val iconUrl = UIConverterFieldUtils.getOpenWeatherIconUrl(iconNumber)
        GlideApp.with(view)
            .load(iconUrl)
            .into(view.futureConditionIcon)
    }



    private fun updateTemperature(view: View) {
        val temp = weatherEntry.main.temp
        val tempFeelsLike = weatherEntry.main.feelsLike
        val tempMin = weatherEntry.main.tempMin
        val tempMax = weatherEntry.main.tempMax

        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            isMetric,
            ForecastApplication.getAppString(R.string.metric_temperature),
            ForecastApplication.getAppString(R.string.imperial_temperature)
        )
        view.futureTemperature.text = "${String.format("%.1f", temp)}$unitAbbreviation"
        view.futureFeelsLikeValue.text = "${String.format("%.1f", tempFeelsLike)}$unitAbbreviation"
        // TODO: commented ForDebug to see all attributes
//        if (tempMax == temp && tempMin == temp) {
//            view.futureMinMaxGroup.visibility = View.GONE
//        } else {
//            view.futureMinMaxGroup.visibility = View.VISIBLE
            view.futureMinTemp.text = "${String.format("%.1f", tempMin)}$unitAbbreviation"
            view.futureMaxTemp.text = "${String.format("%.1f", tempMax)}$unitAbbreviation"
//        }
    }

    private fun updateDate(view: View) {
        view.futureWeatherDate.text = UIConverterFieldUtils.dateTimestampToDateTimeString(weatherEntry.dt)
    }
}


