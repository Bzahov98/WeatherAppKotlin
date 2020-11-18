package com.bzahov.weatherapp.ui.anychartGraphs.specificUtils

import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.SingleValueDataSet
import com.anychart.charts.Cartesian
import com.anychart.charts.CircularGauge
import com.anychart.charts.LinearGauge
import com.anychart.charts.Radar
import com.anychart.core.Chart
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.scales.Base
import com.anychart.scales.Linear
import com.bzahov.weatherapp.internal.ThemperatureUtils.Companion.convertFahrenheitsToCelsius
import com.bzahov.weatherapp.internal.enums.WindDirections
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.CustomDataEntry
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setupAndGetCircularGauge
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setupAndGetLinearGauge
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setupAndGetRadar
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

        fun drawWindGauge(it: CurrentWeatherState): Chart {
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
                .radius("95%")
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
            windSpeedNeedle(gauge)

            return gauge
        }

//        fun drawPercentRadarChartDep(it: CurrentWeatherState): Radar {
//           // val chart = setupAndGetRadar()
//
////            extractRadarChartData(it, chart)
//            chart.defaultSeriesType("area")
//            // force chart to stack values by Y scale.
//
//            chart.yScale().stackMode("percent")
//            // set yAxis settings
//
//            chart.yAxis().stroke("#545f69")
//            chart.yAxis().ticks().stroke("#545f69")
//
//            chart.yGrid(0).palette("[\"gray 0.1\", \"gray 0.2\"]");
//
//            // set yAxis labels settings
//            chart.yAxis().labels()
//                .fontSize(12)
//                .fontColor("#545f69")
//                .format("{%Value}%");
//
//            // set xAxis labels appearance settings
//            var xAxisLabels = chart.xAxis().labels();
//            xAxisLabels.padding(5);
//
//            chart.yScale()
//                .ticks("{interval: 25}")
//                .minimum(0)
//                .maximum(100)
//
//
//            // set chart legend settings
//            chart.legend()
//                .enabled(true)
//                .align("center")
//                .position("center-bottom");
//
//            chart.tooltip().format("{%X}: {%Value}%");
//           // return chart
//
//            return blaBla(it)
//        }

        fun drawPercentRadarChart(it: CurrentWeatherState): Radar {
            val radar = setupAndGetRadar("")

            val yScale = radar.yScale()
            //radar.labels(false)//.anchor(Anchor.RIGHT_BOTTOM)
            yScale.minimum(0.0)
            yScale.maximum(100.0)
            yScale.minimumGap(25.0)
            yScale.maximumGap(25)
            yScale.ticks().interval(25.0).count(4)
            yScale.stackMode("percentage")

            radar.yGrid(0).palette("[\"gray 0.1\", \"gray 0.2\"]");

            //  radar.xAxis().labels().padding(5.0, 5.0, 5.0, 5.0)

            radar.legend()
                .align(Align.CENTER)
                .enabled(false)

            val data: MutableList<DataEntry> = ArrayList()
            data.add(CustomDataEntry("Humidity", it.weatherSingleData.humidity))
            data.add(CustomDataEntry("", 100))
            data.add(CustomDataEntry("Clouds", it.weatherSingleData.cloudcover))

//            val set = Set.instantiate()
//            set.data(data)
//            val shamanData = set.mapAs("{ x: 'x', value: 'value' }")
//            val warriorData = set.mapAs("{ x: 'x', value: 'value2' }")
//            val priestData = set.mapAs("{ x: 'x', value: 'value3' }")

            radar.area(data)
//            radar.marker(data).enabled(true)
//                .type(MarkerType.DIAMOND)
//                .size(6.0)
//            val shamanLine: com.anychart.core.radar.series.Line = radar.line(shamanData)
//            shamanLine.name("Weather")
//                .labels(false)
//            shamanLine.markers()
//                .enabled(true)
//                .type(MarkerType.DIAMOND)
//                .size(6.0)

//            val warriorLine: com.anychart.core.radar.series.Line = radar.line(warriorData)
//            warriorLine.name("Warrior")
//            warriorLine.markers()
//                .enabled(true)
//                .type(MarkerType.CIRCLE)
//                .size(3.0)
//
//            val priestLine: com.anychart.core.radar.series.Line = radar.line(priestData)
//            priestLine.name("Priest")
//            priestLine.markers()
//                .enabled(true)
//                .type(MarkerType.CIRCLE)
//                .size(3.0)

            radar.tooltip()
                .anchor(Anchor.AUTO)
                .format("Value: {%Value}%")
            return radar
        }

        private fun extractRadarChartData(it: CurrentWeatherState, chart: Radar) {
            val data: MutableList<DataEntry> = ArrayList()
            data.add(CustomDataEntry("Cloud Cover", 136))
            data.add(CustomDataEntry("Agility", 79))
//            data.add(CustomDataEntry("Stamina", 149, 173, 101))
//            data.add(CustomDataEntry("Intellect", 135, 33, 202))
//            data.add(CustomDataEntry("Spirit", 158, 64, 196))

            val set = Set.instantiate()
            set.data(data)
            val shamanData = set.mapAs("{ x: 'x', value: 'value' }")

            val shamanLine: com.anychart.core.radar.series.Line = chart.line(shamanData)
            shamanLine.name("Shaman")
            shamanLine.markers()
                .enabled(true)
                .type(MarkerType.CIRCLE)
                .size(2.0)

            chart.data(data)
//                SingleValueDataSet(
////                    arrayOf(
////                        arrayOf("Cloud Cover", it.weatherSingleData.cloudcover),
////                        arrayOf("Humidity", it.weatherSingleData.humidity),
////                        arrayOf("", 0)
////
////                    )
//                    arrayOf(
//                        it.weatherSingleData.cloudcover,
//                        it.weatherSingleData.humidity,
//                        0
//                    )
//                )
//            )
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
                        it.weatherSingleData.speed
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


        private fun windSpeedNeedle(gauge: CircularGauge) {
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

        @Deprecated("old Needle")
        private fun defaultNeedle(gauge: CircularGauge) {

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
                    "if (this.value == 0) return \"${WindDirections.NORTH.shortName}\";" +
                    "if (this.value == 45) return \"${WindDirections.NORTH_EAST.shortName}\";" +
                    "   if (this.value == 90) return \"${WindDirections.EAST.shortName}\";" +
                    "   if (this.value == 135) return \"SE\";" +
                    "   if (this.value == 180) return \"S\";" +
                    "   if (this.value == 225) return \"SW\";" +
                    "   if (this.value == 270) return \"W\";" +
                    "   if (this.value == 315) return \"NW\";" +
                    "   else return this.value;" +
                    "}"
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