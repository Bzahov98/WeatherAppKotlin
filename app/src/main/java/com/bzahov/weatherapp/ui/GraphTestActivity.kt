package com.bzahov.weatherapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.anychart.scales.Linear
import com.bzahov.weatherapp.R
import kotlinx.android.synthetic.main.activity_graph_test.*


class GraphTestActivity : AppCompatActivity() {
    lateinit var anyChartView: AnyChartView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_test)

        anyChartView = any_chart_view
        defaultView(anyChartView)
        //    myFail(anyChartView)
        //anyChartView.refreshDrawableState()
        //okChartWithNegativeValues(anyChartView)

    }

    override fun onResume() {
        super.onResume()
        //defaultView(anyChartView)
        //myFail(anyChartView)

        //    myFail(anyChartView)
        // okChartWithNegativeValues(anyChartView)

        //anyChartView.refreshDrawableState()
    }

    protected fun doublePieChart() {

    }

    companion object {
        private fun okChartWithNegativeValues(anyChartView: AnyChartView) {
            val cartesian = AnyChart.column()

            val data: MutableList<DataEntry> = java.util.ArrayList()
            data.add(ValueDataEntry("Rouge", -80))
            data.add(ValueDataEntry("Foundation", 94))
            data.add(ValueDataEntry("Mascara", 100))
            data.add(ValueDataEntry("Lip gloss", 100))
            data.add(ValueDataEntry("Lipstick", 100))

            val column = cartesian.column(data)

            column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("\${%Value}{groupsSeparator: }")

            cartesian.animation(true)
            cartesian.title("Top 10 Cosmetic Products by Revenue")

            cartesian.yScale().minimum(-35.0).maximum(50.0)

            cartesian.yAxis(0).labels().format("\${%Value}{groupsSeparator: }")

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            cartesian.xAxis(0).title("Product")
            cartesian.yAxis(0).title("Revenue")

            anyChartView.setChart(cartesian)
        }

        private fun myFail(anyChartView: AnyChartView) {
            val chart = AnyChart.column()
            chart.title("Weather next 5 days")

            val data: MutableList<DataEntry> = ArrayList()
            data.add(CustomDataEntry("1.09", 33, 22))
            data.add(CustomDataEntry("2.09", 32, 21))
            data.add(CustomDataEntry("3.09", 30, 19))
            data.add(CustomDataEntry("4.09", 10, -5))
            data.add(CustomDataEntry("5.09", 2, -20))

            var line = chart.lineMarker(0)
            line.value(0)
            line.stroke("2 red")

            chart.container("container")
            chart.data(data)

            anyChartView.setChart(chart)
        }

        public fun defaultView(anyChartView: AnyChartView) {
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
            data.add(CustomDataEntry("P1", 24, 23, -21))
            data.add(CustomDataEntry("P2", 21, 22, -22))
            data.add(CustomDataEntry("P3", 0.2, 21, 21))
            data.add(CustomDataEntry("P4", -23.1, 1, 11))
            data.add(CustomDataEntry("P5", -14.0, 4, 5))

            val set: Set = Set.instantiate()
            set.data(data)
            val lineData: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
            val tempZeroLine: Mapping = set.mapAs("{ x: 'x', value: 'tempZero' }")
            val column1Data: Mapping = set.mapAs("{ x: 'x', value: 'value2' }")
            val column2Data: Mapping = set.mapAs("{ x: 'x', value: 'value3' }")
            val column3Data: Mapping = set.mapAs("{ x: 'x', value: 'value4' }")

            val series1 = cartesian.column(column1Data)
            series1.name("Day")
            //cartesian.crosshair(false)

//                var line = chart.lineMarker();
//                line.value(0);
//                line.stroke("2 red");

            val line: Line = cartesian.line(lineData)
            val tempLine: Line = cartesian.line(tempZeroLine)
            //line.yScale(scalesLinear)

            var series2 = cartesian.column(column2Data)
            series2.name("Night")

            //cartesian.container("container")
            cartesian.column(column3Data)

            cartesian.yScale().minimum(-35.0).maximum(50.0)

            cartesian.yAxis(0).labels().format("\${%Value}{groupsSeparator: }")

            cartesian.tooltip().positionMode(TooltipPositionMode.CHART)
            cartesian.interactivity().hoverMode(HoverMode.BY_SPOT)

            cartesian.xAxis(0).title("Next 5 Days")
            cartesian.yAxis(0).title("Precipitations")
            anyChartView.setChart(cartesian)
        }

        public class CustomDataEntry(
            x: String?,
            value: Number = 0,
            value2: Number? = 0,
            value3: Number? = 0,
            value4: Number? = 0
        ) :

            ValueDataEntry(x, value.toInt() + ZERO_TEMP_CONST) {
            init {
                setValue("value2", value2)
                setValue("value3", value3)
                setValue("value4", value4)
                setValue("tempZero", ZERO_TEMP_CONST)
            }

            companion object {

                val ZERO_TEMP_CONST = 35
            }
        }

        private class WeatherDataEntry(
            x: String?,
            value: Number? = 0,
            value2: Number? = 0,
            value3: Number? = 0,
            value4: Number? = 0
        ) :
            ValueDataEntry(x, value) {
            init {
                setValue("value2", value2)
                setValue("value3", value3)
                setValue("value4", value4)
            }
        }
    }
}