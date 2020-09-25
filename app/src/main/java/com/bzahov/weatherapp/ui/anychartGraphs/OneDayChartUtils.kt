package com.bzahov.weatherapp.ui.anychartGraphs

import android.util.Log
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.HoverMode
import com.anychart.enums.ScaleTypes
import com.anychart.enums.TooltipPositionMode
import com.anychart.scales.Linear
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.ui.anychartGraphs.OneDayChartUtils.Companion.CustomDataEntry.Companion.KEY_CLOUDINESS
import com.bzahov.weatherapp.ui.anychartGraphs.OneDayChartUtils.Companion.CustomDataEntry.Companion.KEY_HUMIDITY
import com.bzahov.weatherapp.ui.anychartGraphs.OneDayChartUtils.Companion.CustomDataEntry.Companion.KEY_PRESSURE
import com.bzahov.weatherapp.ui.anychartGraphs.OneDayChartUtils.Companion.CustomDataEntry.Companion.KEY_RAIN
import com.bzahov.weatherapp.ui.anychartGraphs.OneDayChartUtils.Companion.CustomDataEntry.Companion.KEY_SNOW
import com.bzahov.weatherapp.ui.anychartGraphs.OneDayChartUtils.Companion.CustomDataEntry.Companion.KEY_TEMP
import com.bzahov.weatherapp.ui.anychartGraphs.OneDayChartUtils.Companion.CustomDataEntry.Companion.KEY_TEMP_FEELS_LIKE
import com.bzahov.weatherapp.ui.anychartGraphs.OneDayChartUtils.Companion.CustomDataEntry.Companion.KEY_TEMP_ZERO
import com.bzahov.weatherapp.ui.weather.oneday.OneDayWeatherState

class OneDayChartUtils {
    companion object {

        const val CHART_TYPE_DEFAULT = "DEFAULT"
        const val CHART_TYPE_TEMPERATURE = "TEMPERATURE"
        const val CHART_TYPE_PRECIPITATIONS = "PRECIPITATIONS"

        fun createChart(
            weatherStateData: OneDayWeatherState?,
            chartType: String = CHART_TYPE_DEFAULT
        ): Cartesian {
            val cartesian: Cartesian = AnyChart.cartesian()
            cartesian.autoRedraw(true)

            cartesian.dataArea(true)
            cartesian.animation(true)
            cartesian.crosshair(false)

            cartesian.title("Weather in next 24 hours")

            cartesian.yScale().stickToZero(true)
            //cartesian.yAxis("Sss")


            val scalesLinear: Linear = Linear.instantiate()
//        scalesLinear.minimum(-40.0)
            scalesLinear.maximum(100.0)

            //val extraYAxis = cartesian.yAxis(1.0)
//        extraYAxis.orientation(Orientation.RIGHT)
//            .scale(scalesLinear)
//
            var data: MutableList<DataEntry> = testChartData() // TODO: Debug
            var unitAbbreviation = "C"
            if (weatherStateData != null) {
                data = extractChartDataFromState(weatherStateData, chartType)
                // currentStateData.hourInfoItemsList.last().
                unitAbbreviation = weatherStateData.getUnitAbbreviation()
            } else {

                //data: MutableList<DataEntry> = testData()
            }
            val set: Set = Set.instantiate()
            set.data(data)

            val lineData: Mapping = set.mapAs("{ x: 'x', value: '${KEY_TEMP}' }") // Hour info
            val tempZeroLine: Mapping = set.mapAs("{ x: 'x', value: '${KEY_TEMP_ZERO}' }") // 0
            val column1Data: Mapping = set.mapAs("{ x: 'x', value: 'value2' }") // weatherEntry.rain
            val column2Data: Mapping = set.mapAs("{ x: 'x', value: 'value3' }") // weatherEntry.snow
            val column3Data: Mapping = set.mapAs("{ x: 'x', value: 'value4' }")


//            val series3 = cartesian.column(column3Data)
//            series3.name("Any")

//                var line = chart.lineMarker();
//                line.value(0);
//                line.stroke("2 red");
            val xAxisName = "Next 24 Hours"
            var yAxisName = "Precipitations, mm"
            var series1Name = "Day"
            var series2Name = "Night"
            val line: Line = cartesian.line(lineData)
            val tempLine: Line = cartesian.line(tempZeroLine)
            if (chartType == CHART_TYPE_DEFAULT) {

                //line.yScale(scalesLinear)
            } else {
                series1Name = "Temperature"
                series2Name = "FeelsLike"
                yAxisName = "Temperature, $unitAbbreviation"

                //cartesian.yScale().minimum(-35.0).maximum(50.0)
            }
            val series1 = cartesian.column(column1Data)
            val series2 = cartesian.column(column2Data)

            series1.name(series1Name)
            series2.name(series2Name)

            cartesian.yAxis(0).labels().format("{%Value} $unitAbbreviation")
            //cartesian.yAxis(0).scale(ScaleTypes.GANTT)
            cartesian.tooltip().positionMode(TooltipPositionMode.CHART)
            cartesian.interactivity().hoverMode(HoverMode.SINGLE)

            cartesian.xAxis(0).title(xAxisName)
            cartesian.yAxis(0).title(yAxisName)
            return cartesian
        }

        fun createTemperatureChart(weatherStateData: OneDayWeatherState?): Cartesian {
            val cartesian: Cartesian = AnyChart.cartesian()
            cartesian.autoRedraw(true)

            cartesian.dataArea(true)
            cartesian.animation(true)
            cartesian.crosshair(false)

            cartesian.background().stroke("null")

            cartesian.title("Weather in next 24 hours")

            cartesian.yScale().stickToZero(true)
            //cartesian.yAxis("Sss")


            val scalesLinear: Linear = Linear.instantiate()
//        scalesLinear.minimum(-40.0)
            scalesLinear.maximum(100.0)

            //val extraYAxis = cartesian.yAxis(1.0)
//        extraYAxis.orientation(Orientation.RIGHT)
//            .scale(scalesLinear)
//
            var data: MutableList<DataEntry> = testChartData() // TODO: Debug
            var unitAbbreviation = "C"
            if (weatherStateData != null) {
                data = extractChartDataFromState(weatherStateData, CHART_TYPE_TEMPERATURE)
                // currentStateData.hourInfoItemsList.last().
                unitAbbreviation = weatherStateData.getUnitAbbreviation()
            } else {
                Log.e("OneDayChartUtils", "createTemperatureChart: test data")
                //data: MutableList<DataEntry> = testData()
            }

            val set: Set = Set.instantiate()
            set.data(data)

            val tempLineData: Mapping = set.mapAs("{ x: 'x', value: '${KEY_TEMP}' }") // Hour info
            val feelsLikeLineData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_TEMP_FEELS_LIKE}' }") // weather.feelsLike
            //val tempZeroLine: Mapping = set.mapAs("{ x: 'x', value: '${KEY_TEMP_ZERO}' }")
            val column1Data: Mapping = set.mapAs("{ x: 'x', value: 'value' }") // temperature
            val column2Data: Mapping = set.mapAs("{ x: 'x', value: 'value2' }") // feels like
            val column3Data: Mapping = set.mapAs("{ x: 'x', value: 'value3' }")
//            val series3 = cartesian.column(column3Data)

            val xAxisName = "Next 24 Hours"
            var yAxisName = "Temperature, $unitAbbreviation"
            var series1Name = "Temperature"
            var series2Name = "FeelsLike"

            val tempLine: Line = cartesian.line(tempLineData)
            tempLine.name(series1Name)
            tempLine.labels().format("{%Value} $unitAbbreviation")
            val feelsLikeLine: Line = cartesian.line(feelsLikeLineData)
            feelsLikeLine.name(series2Name)

            //cartesian.yScale().minimum(-35.0).maximum(50.0)
            val series1 = cartesian.column(column1Data)
            series1.name(series1Name)
            val series2 = cartesian.column(column2Data)

            series2.name(series2Name)

            val xAxis = cartesian.xAxis(0)
            val yAxis = cartesian.yAxis(0)
            yAxis.labels().format("{%Value} $unitAbbreviation")
            yAxis.scale(ScaleTypes.LINEAR_COLOR)
            cartesian.tooltip().positionMode(TooltipPositionMode.CHART)
            cartesian.interactivity().hoverMode(HoverMode.SINGLE)

            xAxis.title(xAxisName)
            xAxis.labels().hAlign("center")
            yAxis.title(yAxisName)
            return cartesian
        }

        fun createPrecipitationsChart(
            weatherStateData: OneDayWeatherState?,
            chartType: String = CHART_TYPE_PRECIPITATIONS
        ): Cartesian {
            val cartesian: Cartesian = AnyChart.cartesian()
            cartesian.autoRedraw(true)

            cartesian.dataArea(true)
            cartesian.animation(true)
            cartesian.crosshair(true)

            cartesian.title("Precipitations in next 24 hours")

            //cartesian.yAxis("Sss")


            //val scalesLinear: Linear = Linear.instantiate()
//        scalesLinear.minimum(-40.0)
            // scalesLinear.maximum(100.0)

            //val extraYAxis = cartesian.yAxis(1.0)
//        extraYAxis.orientation(Orientation.RIGHT)
//            .scale(scalesLinear)
//
            var data: MutableList<DataEntry> = ArrayList()// testChartData() // TODO: Debug
            var unitAbbreviation = ",mm"
            if (weatherStateData != null) {
                data = extractChartDataFromState(weatherStateData, chartType)
                // currentStateData.hourInfoItemsList.last().
                //unitAbbreviation = weatherStateData.getUnitAbbreviation()
            } else {

                //data: MutableList<DataEntry> = testData()
            }
            val set: Set = Set.instantiate()
            set.data(data)

            val cloudinessLineData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_CLOUDINESS}' }") // Cloudiness, % - value
            val humidityLineData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_HUMIDITY}' }") // humidity, % - value2
            val pressureLineData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_PRESSURE}' }") // pressure hPa - value3
            val rainColumnData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_RAIN}' }") // rain mm - value4
            val snowColumnData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_SNOW}' }") // snow mm - value5
            //val column3Data: Mapping = set.mapAs("{ x: 'x', value: 'value4' }")


//            val series3 = cartesian.column(column3Data)
//            series3.name("Any")

//                var line = chart.lineMarker();
//                line.value(0);
//                line.stroke("2 red");
            val xAxisName = "Next 24 Hours"
            var yAxisName = "Precipitations, mm"
            var series1Name = "Rain, mm"
            var series2Name = "Snow, mm"
            val line: Line = cartesian.line(cloudinessLineData)
            line.name("Cloudiness, %")
            val hummidityLine: Line = cartesian.line(humidityLineData)
            hummidityLine.name("Humidity, %")
//            val pressureLine: Line = cartesian.line(pressureLineData)
//            pressureLine.name("Pressure, hPa")

            val series1 = cartesian.column(rainColumnData)
            val series2 = cartesian.column(snowColumnData)

            series1.name(series1Name)
            series2.name(series2Name)

            cartesian.yAxis(0).labels().format("{%Value} $unitAbbreviation")
            //cartesian.yAxis(0).scale(ScaleTypes.GANTT)
            cartesian.tooltip().positionMode(TooltipPositionMode.CHART)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            val log = cartesian.yScale(ScaleTypes.LINEAR_COLOR)
            val yScale = cartesian.yScale()
            yScale.softMaximum(100)
            yScale.ticks().interval(10)
            yScale.minorTicks().interval(2)
            yScale.softMinimum(0)
            var ticksArray = arrayOf<String>("0","3","6","9","12","15","20","30","40","50", "75", "100");

            //cartesian.yScale().ticks().set(ticksArray)
            cartesian.xAxis(0).title(xAxisName)
            cartesian.yAxis(0).title(yAxisName)
            return cartesian
        }

        private fun testChartData(): MutableList<DataEntry> {
            val data: MutableList<DataEntry> = ArrayList()
            data.add(CustomDataEntry("P1", 24, 23, -21))
            data.add(CustomDataEntry("P2", 21, 22, -22))
            data.add(CustomDataEntry("P3", 0.2, 21, 21))
            data.add(CustomDataEntry("P4", -23.1, 1, 11))
            data.add(CustomDataEntry("P5", -14.0, 4, 5))
            return data
        }

        private fun extractChartDataFromState(
            state: OneDayWeatherState,
            chartType: String = CHART_TYPE_DEFAULT
        ): MutableList<DataEntry> {
            val data: MutableList<DataEntry> = ArrayList()
            when (chartType) {
                CHART_TYPE_DEFAULT -> {
                    state.hourInfoItemsList.forEach { hourData ->
                        val weatherEntry = hourData.weatherEntry
                        data.add(
                            CustomDataEntry(
                                UIConverterFieldUtils.dateTimestampToHourString(
                                    weatherEntry.dt,
                                    0
                                ), // x
                                weatherEntry.main.temp, // value
                                weatherEntry.rain?.precipitationsForLast3hours,// value 2
                                weatherEntry.snow?.precipitationsForLast3hours // value 3
                            )
                        )
                    }
                }
                CHART_TYPE_TEMPERATURE -> {

                    state.weatherData.forEach { hourData ->
                        data.add(
                            CustomDataEntry(
                                UIConverterFieldUtils.dateTimestampToHourString(
                                    hourData.dt,
                                    0
                                ), // x
                                hourData.main.temp, // value
                                hourData.main.feelsLike // value 2
                            )
                        )
                    }
                }
                CHART_TYPE_PRECIPITATIONS -> {

                    state.weatherData.forEach { hourData ->
                        data.add(
                            CustomDataEntry(
                                UIConverterFieldUtils.dateTimestampToHourString(
                                    hourData.dt,
                                    0
                                ), // x
                                hourData.clouds.all, // value
                                hourData.main.humidity, // value 2
                                hourData.main.pressure, // value 3
                                hourData.rain?.precipitationsForLast3hours,// value 4
                                hourData.snow?.precipitationsForLast3hours // value 5
                            )
                        )
                    }
                }


            }

            return data
        }

        class CustomDataEntry(
            x: String?,
            value: Number = 0,
            value2: Number? = 0,
            value3: Number? = 11,
            value4: Number? = 0,
            value5: Number? = 0
        ) :

            ValueDataEntry(x, value.toInt() + ZERO_TEMP_CONST) {
            init {
                setValue("value2", value2)
                setValue("value3", value3)
                setValue("value4", value4)
                setValue("value5", value5)
                setValue(KEY_TEMP_ZERO, ZERO_TEMP_CONST)
            }

            companion object {
                // DEFAULT
                const val ZERO_TEMP_CONST = 0
                const val KEY_TEMP_ZERO = "value5OrZero"

                // TEMPERATURE
                const val KEY_TEMP = "value"
                const val KEY_TEMP_FEELS_LIKE = "value2"

                // PRECIPITATIONS
                const val KEY_CLOUDINESS = "value"
                const val KEY_HUMIDITY   = "value2"
                const val KEY_PRESSURE   = "value3"
                const val KEY_RAIN       = "value4"
                const val KEY_SNOW       = "value5"
            }
        }
    }
}