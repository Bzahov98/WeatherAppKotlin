package com.bzahov.weatherapp.ui.anychartGraphs.specificUtils

import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.SingleValueDataSet
import com.anychart.charts.LinearGauge
import com.anychart.enums.Anchor
import com.anychart.enums.Orientation
import com.anychart.enums.Position
import com.anychart.scales.Base
import com.anychart.scales.Linear
import com.bzahov.weatherapp.internal.ThemperatureUtils.Companion.convertFahrenheitsToCelsius
import com.bzahov.weatherapp.ui.weather.current.CurrentWeatherState

class CurrentWeatherChartUtils {
    companion object {
        const val TAG = "CurrentWeatherChartUtils"

        fun drawThermometer(it: CurrentWeatherState): LinearGauge {
            val linearGauge = AnyChart.linear()

            linearGauge.thermometer(0)
                .name("Thermometer")
                .id(1)

            extractChartDataFromState(it, linearGauge)

            //linearGauge.data())

            linearGauge.tooltip()
                .useHtml(true)
                .format(
                    jsGetThermometerTooltip()
                )

            linearGauge.jsLabel(0, Position.LEFT_TOP, Anchor.LEFT_TOP, isMetric = true)
            linearGauge.jsLabel(1, Position.RIGHT_TOP, Anchor.RIGHT_TOP, isMetric = false)

            val scale: Base = linearGauge.scale()
                .minimum(-30)
                .maximum(40)

            linearGauge.axis(0).scale(scale)
            linearGauge.axis(0)
                .offset("-1%")
                .width("0.5%")

            linearGauge.axis(0).labels()
                .format("{%Value}&deg;")
                .useHtml(true)


            linearGauge.axis(0).minorTicks(true)
            linearGauge.axis(0).labels()
                .format(
                    jsAxisLabelStyle()
                )
                .useHtml(true)

            linearGauge.axis(1).minorTicks(true)
            linearGauge.axis(1).labels()
                .format(
                    jsAxisLabelStyle()
                )
                .useHtml(true)
            linearGauge.axis(1)
                .offset("3.5%")
                .orientation(Orientation.RIGHT)

            val linear = Linear.instantiate()
            linear.minimum(-20)
                .maximum(100)
//                .setTicks
            //                .setTicks
            linearGauge.axis(1).scale(linear)

            return linearGauge
        }

        private fun extractChartDataFromState(
            it: CurrentWeatherState,
            linearGauge: LinearGauge
        ) {
            var tempValue = it.weatherSingleData.temperature
            var feelsLikeValue = it.weatherSingleData.feelslike


            if(!it.isMetric){
                tempValue = convertFahrenheitsToCelsius(tempValue)
                feelsLikeValue = convertFahrenheitsToCelsius(feelsLikeValue)
            }
            if (tempValue > feelsLikeValue) {
                linearGauge.addPointer(SingleValueDataSet(arrayOf(feelsLikeValue)))
                linearGauge.addPointer(SingleValueDataSet(arrayOf(tempValue)))
            } else {
                linearGauge.addPointer(SingleValueDataSet(arrayOf(tempValue)))
                linearGauge.addPointer(SingleValueDataSet(arrayOf(feelsLikeValue)))
            }
        }


        fun LinearGauge.jsLabel (index: Int = 0, position: Position, anchor: Anchor, isMetric : Boolean, offsetX : String= "25%", offsetY : String = "20px", fontColor : String = "black", fontSize : Int = 15){
            val text : String = if(isMetric) "C&deg;" else "F&deg;"
            this.label(index)
                .useHtml(true)
                .text(text)
                .position(position/*Position.LEFT_TOP*/)
                .anchor(anchor/*Anchor.LEFT_TOP*/)
                .offsetX(offsetX)
                .offsetY(offsetY)
                .fontColor(fontColor)
                .fontSize(fontSize)
        }

        private fun jsAxisLabelStyle(): String {
            return ("function () {\n" +
                    "    return '<span style=\"color:black;\">' + this.value + '&deg;</span>'\n" +
                    "  }")
        }

        private fun jsGetThermometerTooltip(): String {
            return "function () {\n" +
                    "          return this.value + '&deg;' + 'C' +\n" +
                    "            ' (' + (this.value * 1.8 + 32).toFixed(1) +\n" +
                    "            '&deg;' + 'F' + ')'\n" +
                    "    }"
        }
    }
}