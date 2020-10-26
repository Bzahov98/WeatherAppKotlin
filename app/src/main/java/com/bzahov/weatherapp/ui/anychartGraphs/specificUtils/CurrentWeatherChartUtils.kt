package com.bzahov.weatherapp.ui.anychartGraphs.specificUtils

import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.SingleValueDataSet
import com.anychart.charts.Cartesian
import com.anychart.charts.LinearGauge
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.scales.Base
import com.anychart.scales.Linear
import com.bzahov.weatherapp.internal.ThemperatureUtils.Companion.convertFahrenheitsToCelsius
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setupAndGetLinearGauge
import com.bzahov.weatherapp.ui.base.states.AbstractState
import com.bzahov.weatherapp.ui.weather.current.CurrentWeatherState
import com.bzahov.weatherapp.ui.weather.oneday.OneDayDialogFragment

class CurrentWeatherChartUtils {
    companion object {
        const val TAG = "CurrentWeatherChartUtils"

        fun drawThermometer(it: CurrentWeatherState): LinearGauge {
            val thermometer = "Thermometer"
            val linearGauge = setupAndGetLinearGauge(thermometer)

            linearGauge.thermometer(0)
                .name(thermometer)
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

        //Unused
        @Deprecated("unused")
        private fun drawChart(it: CurrentWeatherState) : Cartesian{

            val cartesian: Cartesian = AnyChart.cartesian()

            cartesian.animation(true)

            cartesian.title("Weather in next 5 days")

            cartesian.yScale().stickToZero(true)
            //cartesian.yAxis("Sss")


            val scalesLinear: Linear = Linear.instantiate()
//        scalesLinear.minimum(-40.0)
            scalesLinear.maximum(75.0)

            val extraYAxis = cartesian.yAxis(1.0)
//        extraYAxis.orientation(Orientation.RIGHT)
//            .scale(scalesLinear)
//

            val data: MutableList<DataEntry> = ArrayList()
            data.add(OneDayDialogFragment.CustomDataEntry("P1", 24, 23, -21))
            data.add(OneDayDialogFragment.CustomDataEntry("P2", 21, 22, -22))
            data.add(OneDayDialogFragment.CustomDataEntry("P3", 0.2, 21, 21))
            data.add(OneDayDialogFragment.CustomDataEntry("P4", -23.1, 1, 11))
            data.add(OneDayDialogFragment.CustomDataEntry("P5", -14.0, 4, 5))

            val set: Set = Set.instantiate()
            set.data(data)
            val lineData: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
            val tempZeroLine: Mapping = set.mapAs("{ x: 'x', value: 'tempZero' }")
            val column1Data: Mapping = set.mapAs("{ x: 'x', value: 'value2' }")
            val column2Data: Mapping = set.mapAs("{ x: 'x', value: 'value3' }")
            val column3Data: Mapping = set.mapAs("{ x: 'x', value: 'value4' }")

            val series1 = cartesian.column(column1Data)
            series1.name("series 1")
            //cartesian.crosshair(false)

//                var line = chart.lineMarker();
//                line.value(0);
//                line.stroke("2 red");

            val line: Line = cartesian.line(lineData)
            val tempLine: Line = cartesian.line(tempZeroLine)
            //line.yScale(scalesLinear)

            var series2 = cartesian.column(column2Data)
            series2.name("series 2")

            //cartesian.container("container")
            cartesian.column(column3Data)

            cartesian.yScale().minimum(-35.0).maximum(50.0)

            cartesian.yAxis(0).labels().format("\${%Value}{groupsSeparator: }")

            cartesian.tooltip().positionMode(TooltipPositionMode.CHART)
            cartesian.interactivity().hoverMode(HoverMode.BY_SPOT)

            cartesian.xAxis(0).title("Product")
            cartesian.yAxis(0).title("Revenue")
            return cartesian
        }

        fun s(view: AnyChartView, dataState : AbstractState) {
            APIlib.getInstance().setActiveAnyChartView(view)
            view.setDebug(true)
            view.setChart(drawChart(dataState as CurrentWeatherState))
        }


    }
}