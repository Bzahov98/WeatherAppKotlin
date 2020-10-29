package com.bzahov.weatherapp.ui.anychartGraphs

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.anychart.APIlib
import com.anychart.AnyChartView
import com.anychart.charts.Cartesian
import com.anychart.core.Chart
import com.bzahov.weatherapp.ui.weather.current.CurrentWeatherState
import com.bzahov.weatherapp.ui.weather.oneday.OneDayWeatherState

class AnyChartGraphsFactory {

    companion object {
        fun showDialog(context: Context, layoutId: Int, chartId: Int, weatherStateData: OneDayWeatherState?, drawingChart: ((OneDayWeatherState?)-> Cartesian)) {

            val dialog = MaterialDialog(context)
                .customView(layoutId)
                .cornerRadius(5.5f)

            val chartView: AnyChartView = dialog.findViewById(chartId)
            APIlib.getInstance().setActiveAnyChartView(chartView)
            chartView.setDebug(true)
            chartView.setChart(drawingChart.invoke(weatherStateData))
            dialog.show()
        }

        fun initChart(it: CurrentWeatherState, view: AnyChartView, drawingChart:( (CurrentWeatherState) -> Chart)){
            APIlib.getInstance().setActiveAnyChartView(view)
            view.setDebug(true)

            view.setChart(drawingChart.invoke(it))
        }
    }
}