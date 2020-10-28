package com.bzahov.weatherapp.ui.anychartGraphs.specificUtils

import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.SingleValueDataSet
import com.anychart.charts.Cartesian
import com.anychart.charts.CircularGauge
import com.anychart.charts.LinearGauge
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.scales.Base
import com.anychart.scales.Linear
import com.bzahov.weatherapp.internal.ThemperatureUtils.Companion.convertFahrenheitsToCelsius
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.CustomDataEntry
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setupAndGetCircularGauge
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setupAndGetLinearGauge
import com.bzahov.weatherapp.ui.base.states.AbstractState
import com.bzahov.weatherapp.ui.weather.current.CurrentWeatherState
import com.bzahov.weatherapp.ui.weather.oneday.OneDayDialogFragment

class CurrentWeatherChartUtils {
    companion object {
        const val TAG = "CurrentWeatherChartUtils"

        fun drawThermometer(it: CurrentWeatherState): LinearGauge {
            val thermometer = "Temperature"
            val linearGauge = setupAndGetLinearGauge(thermometer)


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

        fun drawWindGauge(it: CurrentWeatherState): CircularGauge {
            val gauge = setupAndGetCircularGauge("Wind")
//            gauge.fill("white")
//                .stroke(null)
//                .tooltip(false);

            extractWindChartDataFromState(it, gauge)

            val axis = gauge.axis(0)

            axis.scale()
                .minimum(0)
                .maximum(360)
                .ticks("{interval: 45}")
                .minorTicks("{interval: 45}");

            axis
                .startAngle(0)
                .fill("#CECECE")
                .radius("100%")
                .sweepAngle(360);

            axis.ticks()
                .enabled(true)
                .length(20)
                .stroke("2 #CECECE")
                .position("outside")
                .type(
                    "function(path, x, y, radius) {" +
                            "path.moveTo(x, y - radius / 2).lineTo(x, y + radius / 2).close();" +
                            " return path;" +
                            "}"
                );

            axis.labels()
                .fontSize(10)
                .position("outside")
                .format(jsDirectionFunction());


            //defaultNeddle(gauge)
            windSpeedNedle(gauge)

            return gauge
        }

        private fun windSpeedNedle(gauge: CircularGauge) {
            // wind speed
            val axis = gauge.axis(1)
            val innerNeddleLayout = "55%"
            val length = 3

            axis
                .fill("#7c868e")
                .startAngle(260)
                .radius(innerNeddleLayout)
                .sweepAngle(200)
                .width(length)
            ;
            val ticks = axis.ticks().apply {
                enabled(true)
                type("line")
                fill("#7c868e")
                length(length)
                position("outside")
            }

            axis
                .labels()
                .fontSize(9)
                .padding(2)
                .position("outside")
                .format("{%Value}");


            var ticksArray = arrayOf<String>(
                "0",
                "5",
                "10",
                "15",
                "20",
                "25"
            );
            // inside ticks
            axis.labels()
            axis
                .scale().minimum(0).maximum(25)
                .ticks().set(ticksArray)
            //axis.scale().minorTicks().apply { interval(5) };

            val radius = "10%"
            gauge.cap()
                .radius(radius)
                .fill("#1976d2")
                .enabled(true)
                .stroke(null);

            // wind direction neddle
            gauge
                .marker(0)
                .fill("#64b5f6")
                .stroke(null)
                .axisIndex(0)
                .size("14%")
                .zIndex(120)
                .radius("98%");

            //arrow neddle inside

            gauge
                .needle(2)
                .fill("#1976d2")
                .stroke("null")//"#64b5f6")
                .axisIndex(1)
                //.middleRadius("16%")
                .startRadius(radius)
                .endRadius(innerNeddleLayout)
                .startWidth("2%")
                .middleWidth("1")
                .endWidth("0");
        }

        private fun defaultNeddle(gauge: CircularGauge) {

            //            gauge.needle(0) // >
//                .fill("#FFFFF")
//                .stroke("#64b5f6")
//                .startRadius("45%")
//                .middleRadius("5%")
//                .endRadius("-85%")
//                .startWidth("0%")
//                .endWidth("0%")
//                .middleWidth("20%");

            gauge.needle(1) // >
                .fill("#FFFFF")
                .stroke("#64b5f6")
                .startRadius("-45%")
                .middleRadius("10%")
                .endRadius("80%")
                .startWidth("15%")
                .endWidth("0%")
                .middleWidth("20%");

            gauge.cap()
                .radius("45%")
                .enabled(true)
                .fill("#fff")
                .stroke("#CECECE");

            gauge.label(0)
                .hAlign("center")
                .anchor("center")
                .text(
                    "{%value}"
                )
                .useHtml(true);
        }


        private fun jsDirectionFunction(): String {
            return "function() {" +
                    "if (this.value == 0) return \"N\";" +
                    "if (this.value == 45) return \"NE\";" +
                    "   if (this.value == 90) return \"E\";" +
                    "   if (this.value == 135) return \"SE\";" +
                    "   if (this.value == 180) return \"S\";" +
                    "   if (this.value == 225) return \"SW\";" +
                    "   if (this.value == 270) return \"W\";" +
                    "   if (this.value == 315) return \"NW\";" +
                    "   else return this.value;" +
                    "}"
        }

        private fun extractWindChartDataFromState(it: CurrentWeatherState, gauge: CircularGauge) {
            val data: MutableList<DataEntry> = ArrayList()
            val set: Set = Set.instantiate()


/*
            val windSpeedData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_WIND_SPEED}' }") // wind speed - value

            val windDegreeData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_WIND_DEGREE}' }") // wind degree, 0 - value2
*/

//            data.add(
//                CustomDataEntry(
//                    it.currentWind,
//                    it.weatherSingleData.degree,
//                    it.weatherSingleData.speed
//                )
//            )

            gauge.data(
                SingleValueDataSet(
                    arrayOf(
                        it.weatherSingleData.degree,
                        it.weatherSingleData.speed+10
                    )
                ) 
            )
//            set.data(data)
//            gauge.data(data)
        }

        private fun extractChartDataFromState(
            it: CurrentWeatherState,
            linearGauge: LinearGauge
        ) {
            val temperature = "Temperature"
            val feelsLike = "FeelsLike"

            linearGauge.thermometer(0)
                .name(temperature)
                .id(1)
//             linearGauge.thermometer(1)
//                .name(feelsLike)
//                .id(2)


            var tempValue = it.weatherSingleData.temperature
            var feelsLikeValue = it.weatherSingleData.feelslike

            if (!it.isMetric) {
                tempValue = convertFahrenheitsToCelsius(tempValue)
                feelsLikeValue = convertFahrenheitsToCelsius(feelsLikeValue)
            }
//            if (tempValue > feelsLikeValue) {

            val data: MutableList<DataEntry> = ArrayList()
            data.add(CustomDataEntry(temperature, tempValue))
            data.add(CustomDataEntry(feelsLike, feelsLikeValue))
            linearGauge.data(data)
//                linearGauge.addPointer(SingleValueDataSet(arrayOf(feelsLikeValue)))
//                linearGauge.getPointerAt(0).name("Feels like:")

//            } else {
//                linearGauge.data(SingleValueDataSet(arrayOf(tempValue, feelsLikeValue)))
//                linearGauge.addPointer(SingleValueDataSet(arrayOf(feelsLikeValue)))
//            }

        }


        fun LinearGauge.jsLabel(
            index: Int = 0,
            position: Position,
            anchor: Anchor,
            isMetric: Boolean,
            offsetX: String = "25%",
            offsetY: String = "20px",
            fontColor: String = "black",
            fontSize: Int = 15
        ) {
            val text: String = if (isMetric) "C&deg;" else "F&deg;"
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
        private fun drawChart(it: CurrentWeatherState): Cartesian {

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

        fun s(view: AnyChartView, dataState: AbstractState) {
            APIlib.getInstance().setActiveAnyChartView(view)
            view.setDebug(true)
            view.setChart(drawChart(dataState as CurrentWeatherState))
        }

        // Wind
        const val KEY_WIND_DESCRIPTION = "x"
        const val KEY_WIND_DEGREE = "value"
        const val KEY_WIND_SPEED = "value2"
    }
}