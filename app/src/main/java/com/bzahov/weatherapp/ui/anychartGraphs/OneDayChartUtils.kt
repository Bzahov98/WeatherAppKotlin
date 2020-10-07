package com.bzahov.weatherapp.ui.anychartGraphs

import android.util.Log
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.graphics.vector.StrokeLineCap
import com.anychart.graphics.vector.StrokeLineJoin
import com.anychart.scales.Linear
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.internal.enums.WindDirections
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.colorizeAxesBackground
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.hideSeriesTooltip
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setAxisLabelName
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setSeriesTooltip
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setupAndGetBaseCartesian
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.setupTooltipAndInteractivity
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsUtils.Companion.testChartData
import com.bzahov.weatherapp.ui.weather.oneday.OneDayWeatherState

class OneDayChartUtils {
    companion object {
        private const val TAG = "OneDayChartUtils"
        private const val CHART_TYPE_DEFAULT = "DEFAULT"
        private const val CHART_TYPE_TEMPERATURE = "TEMPERATURE"
        private const val CHART_TYPE_PRECIPITATIONS = "PRECIPITATIONS"
        private const val CHART_TYPE_WIND = "WIND"

        fun createChart(
            weatherStateData: OneDayWeatherState?,
            chartType: String = CHART_TYPE_DEFAULT
        ): Cartesian {
            val chartTitle = getAppString(R.string.graphs_default_title)

            val cartesian: Cartesian = setupAndGetBaseCartesian(chartTitle)
            cartesian.yScale().stickToZero(true)

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

            val lineData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_TEMPERATURE_TEMP}' }") // Hour info
            val tempZeroLine: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_DEFAULT_TEMP_ZERO}' }") // 0
            val column1Data: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_DEFAULT_RAIN}' }") // weatherEntry.rain value2
            val column2Data: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_DEFAULT_SNOW}' }") // weatherEntry.snow value3
//            val column3Data: Mapping = set.mapAs("{ x: 'x', value: 'value4' }")


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
                .name(series1Name)
            val series2 = cartesian.column(column2Data)
                .name(series2Name)

            val xAxis = cartesian.xAxis(0)
            val yAxis = cartesian.yAxis(0)

            yAxis.setAxisLabelName(unitAbbreviation)
            //cartesian.yAxis(0).scale(ScaleTypes.GANTT)
            cartesian.tooltip().positionMode(TooltipPositionMode.CHART)
            cartesian.interactivity().hoverMode(HoverMode.SINGLE)

            xAxis.title(xAxisName)
            yAxis.title(yAxisName)
            return cartesian
        }

        fun createTemperatureChart(weatherStateData: OneDayWeatherState?): Cartesian {
            val chartTitle = getAppString(R.string.graphs_temperature_title)
            val cartesian: Cartesian = setupAndGetBaseCartesian(chartTitle)
            cartesian.yScale().stickToZero(true)

            cartesian.background().stroke("blue")


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

            val temperatureData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_TEMPERATURE_TEMP}' }") // Hour info
            val feelsLikeTemperatureData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_TEMPERATURE_FEELS_LIKE}' }") // weather.feelsLike oC oF

            val xAxisName = "Next 24 Hours"
            var yAxisName = "Temperature, $unitAbbreviation"
            var series1Name = "Temperature"
            var series2Name = "FeelsLike"

            val tempLine: Line = cartesian.line(temperatureData)
            tempLine.name(series1Name)
            tempLine.labels().format("{%Value} $unitAbbreviation")
            val feelsLikeLine: Line = cartesian.line(feelsLikeTemperatureData)
            feelsLikeLine.name(series2Name)

            //cartesian.yScale().minimum(-35.0).maximum(50.0)
            val series1 = cartesian.column(temperatureData)
            series1.name(series1Name)
            val series2 = cartesian.column(feelsLikeTemperatureData)

            series2.name(series2Name)

            val xAxis = cartesian.xAxis(0)
            val yAxis = cartesian.yAxis(0)
            yAxis.setAxisLabelName(unitAbbreviation)
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
                val xAxisName = getAppString(R.string.graphs_default_axis_x_name)
                val yAxisName = getAppString(R.string.graphs_precipitation_axis_y_name_precip)
                val series1Name = getAppString(R.string.graphs_precipitation_series_rain)
                val series2Name = getAppString(R.string.graphs_precipitation_series_snow)
                val unitAbbreviation =
                    getAppString(R.string.graphs_precipitation_rain_unit_abbreviation)
                val chartTitle = getAppString(R.string.graphs_precipitation_title)

                val cartesian: Cartesian = setupAndGetBaseCartesian(chartTitle)

                var data: MutableList<DataEntry> = ArrayList()// testChartData() // TODO: Debug

                if (weatherStateData != null) {
                    data = extractChartDataFromState(weatherStateData, chartType)
                    // currentStateData.hourInfoItemsList.last().
                    //unitAbbreviation = weatherStateData.getUnitAbbreviation()
                } else {
                    Log.e(TAG, "createPrecipitationsChart: weatherStateData is null")
                    cartesian.label("No DATA")
                    return cartesian
                }

                val xAxis = cartesian.xAxis(0)
                val yAxis = cartesian.yAxis(0)
                val yAxis1 = cartesian.yAxis(1)

                xAxis
                    .title(xAxisName)
                    .staggerMode(true)
                    .staggerMaxLines(2)
                yAxis.title(yAxisName)
                yAxis.setAxisLabelName(unitAbbreviation)
                yAxis1.setAxisLabelName("%")


                val percentYAxis =
                    yAxis1.title(getAppString(R.string.graphs_precipitation_axis_y_namecloud_humid))
                        .orientation(Orientation.RIGHT)
                val lineMarker = cartesian.lineMarker(0)
                    .axis(percentYAxis)
                    .value(85)
                    //.layout("85%")
                    .stroke("#A5B3B3", 1, "5 2", StrokeLineJoin.ROUND, StrokeLineCap.ROUND)


                val set: Set = Set.instantiate()
                set.data(data)

            val cloudinessLineData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_PRECIPITATIONS_CLOUDINESS}' }") // Cloudiness, % - value
            val humidityLineData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_PRECIPITATIONS_HUMIDITY}' }") // humidity, % - value2
            val pressureLineData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_PRECIPITATIONS_PRESSURE}' }") // pressure hPa - value3
            val rainColumnData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_PRECIPITATIONS_RAIN}' }") // rain mm - value4
            val snowColumnData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_PRECIPITATIONS_SNOW}' }") // snow mm - value5
            //val column3Data: Mapping = set.mapAs("{ x: 'x', value: 'value4' }")


//            val series3 = cartesian.column(column3Data)
//            series3.name("Any")

//                var line = chart.lineMarker();
//                line.value(0);
//                line.stroke("2 red");


            val line: Line = cartesian.line(cloudinessLineData)
            line.name(getAppString(R.string.graphs_precipitation_line_cloudiness))
            line.labels().enabled()

            val humidityLine: Line = cartesian.line(humidityLineData)
            humidityLine.name(getAppString(R.string.graphs_precipitation_line_humidity))
            humidityLine.labels().enabled()
//            val pressureLine: Line = cartesian.line(pressureLineData)
//            pressureLine.name("Pressure, hPa")

            val series1 = cartesian.column(rainColumnData)
            val series2 = cartesian.column(snowColumnData)

            series1.name(series1Name)
            series2.name(series2Name)


            //cartesian.yAxis(0).scale(ScaleTypes.GANTT)

            //tooltipSettings(cartesian, unitAbbreviation)
            setupTooltipAndInteractivity(cartesian)

            setSeriesTooltip(series1, Anchor.RIGHT_BOTTOM)
            setSeriesTooltip(series2, Anchor.LEFT_BOTTOM)
            setSeriesTooltip(line, Anchor.CENTER)
            setSeriesTooltip(humidityLine, Anchor.AUTO)
            //

            val log = cartesian.yScale(ScaleTypes.LINEAR_COLOR)
            val yScale = cartesian.yScale()
            yScale.softMaximum(100)
            yScale.ticks().interval(10)
            yScale.minorTicks().interval(2)
            yScale.softMinimum(0)
            var ticksArray = arrayOf<String>(
                "0",
                "3",
                "6",
                "9",
                "12",
                "15",
                "20",
                "30",
                "40",
                "50",
                "75",
                "100"
            );

            colorizeAxesBackground(xAxis)
            cartesian.yScale().ticks().set(ticksArray)

            return cartesian
        }

        fun createWindChart(
                weatherStateData: OneDayWeatherState?,
                chartType: String = CHART_TYPE_WIND
            ): Cartesian {
                val xAxisName = getAppString(R.string.graphs_default_axis_x_name)
                val yAxisName = getAppString(R.string.graphs_wind_axis_y_name_speed)
                val series1Name = getAppString(R.string.graphs_wind_series_speed)
                val series2Name = getAppString(R.string.graphs_wind_series_degree)
                val unitAbbreviation =
                    getAppString(R.string.graphs_wind_speed_unit_abbreviation)
                val chartTitle = getAppString(R.string.graphs_wind_title)

                val cartesian: Cartesian = setupAndGetBaseCartesian(chartTitle)

                var data: MutableList<DataEntry> = ArrayList()// testChartData() // TODO: Debug

                if (weatherStateData != null) {
                    data = extractChartDataFromState(weatherStateData, chartType)
                    // currentStateData.hourInfoItemsList.last().
                    //unitAbbreviation = weatherStateData.getUnitAbbreviation()
                } else {
                    Log.e(TAG, "createwindChart: weatherStateData is null")
                    cartesian.label("No DATA")
                    return cartesian
                }

                val xAxis = cartesian.xAxis(0)
                val yAxis = cartesian.yAxis(0)
                //val yAxis1 = cartesian.yAxis(1)

                xAxis
                    .title(xAxisName)
                    .staggerMode(true)
                    .staggerMaxLines(2)
                yAxis.title(yAxisName)
                yAxis.setAxisLabelName(unitAbbreviation)
                //yAxis1.setAxisLabelName("%")


//                val percentYAxis =
//                    yAxis1.title(getAppString(R.string.graphs_precipitation_axis_y_namecloud_humid))
//                        .orientation(Orientation.RIGHT)
//                val lineMarker = cartesian.lineMarker(0)
//                    .axis(percentYAxis)
//                    .value(85)
//                    //.layout("85%")
//                    .stroke("#A5B3B3", 1, "5 2", StrokeLineJoin.ROUND, StrokeLineCap.ROUND)


                val set: Set = Set.instantiate()
                set.data(data)

            val windSpeedData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_WIND_SPEED}' }") // wind speed - value
            val windDegreeData: Mapping =
                set.mapAs("{ x: 'x', value: '${KEY_WIND_DEGREE}' }") // wind degree, 0 - value2



//            val series3 = cartesian.column(column3Data)
//            series3.name("Any")

//                var line = chart.lineMarker();
//                line.value(0);
//                line.stroke("2 red");


            val line: Line = cartesian.line(windSpeedData)
            line.name(getAppString(R.string.graphs_wind_line_speed))
            line.labels().enabled(false)
            line.legendItem().disabled()

//            val humidityLine: Line = cartesian.line(windDegreeData)
//            humidityLine.name(getAppString(R.string.graphs_precipitation_line_humidity))
//            humidityLine.labels().enabled()
//            val pressureLine: Line = cartesian.line(pressureLineData)
//            pressureLine.name("Pressure, hPa")

            val series1 = cartesian.column(windSpeedData)
            //val series2 = cartesian.column(windDegreeData)

            series1.name(series1Name)
//            series2.name(series2Name)


            //cartesian.yAxis(0).scale(ScaleTypes.GANTT)

            //tooltipSettings(cartesian, unitAbbreviation)
            setupTooltipAndInteractivity(cartesian)

            setSeriesTooltip(series1, Anchor.RIGHT_BOTTOM,"Wind Speed: {%value}$unitAbbreviation , <br> <b>Wind direction: {%$KEY_WIND_DEGREE}</b>")
            hideSeriesTooltip(line)
//            setSeriesAnchor(series2, Anchor.LEFT_BOTTO    M)
            //setSeriesAnchor(line, Anchor.CENTER)
//            setSeriesAnchor(humidityLine, Anchor.AUTO)
            //

            val log = cartesian.yScale(ScaleTypes.LINEAR_COLOR)
            val yScale = cartesian.yScale()
            //yScale.softMaximum(100)
//            yScale.ticks().interval(10)
//            yScale.minorTicks().interval(2)
            yScale.softMinimum(0)
            colorizeAxesBackground(xAxis)

            return cartesian
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
                            AnyChartGraphsUtils.Companion.CustomDataEntry(
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
                            AnyChartGraphsUtils.Companion.CustomDataEntry(
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
                CHART_TYPE_WIND -> {

                    state.weatherData.forEach { hourData ->
                        data.add(
                            AnyChartGraphsUtils.Companion.CustomDataEntry(
                                UIConverterFieldUtils.dateTimestampToHourString(
                                    hourData.dt,
                                    0
                                ), // x
                                hourData.wind.speed, // value
                                stringValue = WindDirections.getDescriptionStringByDouble(hourData.wind.deg) // hourData.wind.deg // value 2
                            )
                        )
                    }
                }
                CHART_TYPE_PRECIPITATIONS -> {

                    state.weatherData.forEach { hourData ->
                        data.add(
                            AnyChartGraphsUtils.Companion.CustomDataEntry(
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

        // DEFAULT
        const val ZERO_TEMP_CONST = 0
        const val KEY_DEFAULT_TEMP_ZERO = "value5OrZero"
        const val KEY_DEFAULT_RAIN = "value2"
        const val KEY_DEFAULT_SNOW = "value3"

        // TEMPERATURE
        const val KEY_TEMPERATURE_TEMP = "value"
        const val KEY_TEMPERATURE_FEELS_LIKE = "value2"

        // PRECIPITATIONS
        const val KEY_PRECIPITATIONS_CLOUDINESS = "value"
        const val KEY_PRECIPITATIONS_HUMIDITY = "value2"
        const val KEY_PRECIPITATIONS_PRESSURE = "value3"
        const val KEY_PRECIPITATIONS_RAIN = "value4"
        const val KEY_PRECIPITATIONS_SNOW = "value5"

        // Wind
        const val KEY_WIND_SPEED = "value"
        const val KEY_WIND_DEGREE = "stringValue"


    }
}