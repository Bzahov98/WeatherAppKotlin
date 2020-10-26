package com.bzahov.weatherapp.ui.anychartGraphs

import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Base
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.TooltipDisplayMode
import com.anychart.enums.TooltipPositionMode
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.OneDayChartUtils.Companion.KEY_DEFAULT_TEMP_ZERO
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.OneDayChartUtils.Companion.ZERO_TEMP_CONST

class AnyChartGraphsUtils {
    companion object{
        class CustomDataEntry(
            x: String?,
            value: Number = 0,
            value2: Number? = 0,
            value3: Number? = 0,
            value4: Number? = 0,
            value5: Number? = 0,
            stringValue: String = ""
        ) :

            ValueDataEntry(x, value.toInt() + ZERO_TEMP_CONST) {
            init {
                setValue("value2", value2)
                setValue("value3", value3)
                setValue("value4", value4)
                setValue("value5", value5)
                setValue("stringValue", stringValue)
                setValue(KEY_DEFAULT_TEMP_ZERO, ZERO_TEMP_CONST)
            }
        }

        fun setupAndGetBaseCartesian(chartTitle: String): Cartesian {
            val cartesian: Cartesian = AnyChart.cartesian()
            cartesian.autoRedraw(true)
            cartesian.dataArea(true)
            cartesian.animation(true)
            cartesian.crosshair(false)
            cartesian.legend(true)
            cartesian.title(chartTitle)
            return cartesian
        }

        fun com.anychart.core.axes.Linear.setAxisLabelName(unitAbbreviation: String){
            this.labels().format("{%Value} $unitAbbreviation")
        }

        fun colorizeAxesBackground(axis: com.anychart.core.axes.Linear) {
            val xLabelsBackground = axis.labels().background();
            xLabelsBackground.enabled(true);
            xLabelsBackground.stroke("#cecece");
            xLabelsBackground.cornerType("round");
            xLabelsBackground.corners(5);
        }

        fun hideSeriesTooltip(series: Base){
            val tooltip = series.tooltip();
            tooltip.enabled(false)
        }

        fun setSeriesTooltip(series: Base, positionAnchor: Anchor, formatString :String= "", title: String? = null) {
            val tooltip1 = series.tooltip();
            tooltip1.anchor(positionAnchor);
            when {
                formatString != "" -> {
                    tooltip1.useHtml(true);
                    tooltip1.format(formatString)
                }
                title != null -> {
                    tooltip1.title(title)
                }
                else ->
                    tooltip1.title(false)
            };

            tooltip1.separator(false);
        }
        fun setupTooltipAndInteractivity(cartesian: Cartesian) {
            cartesian.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .displayMode(TooltipDisplayMode.SEPARATED)
            cartesian.interactivity().hoverMode(HoverMode.BY_X).selectionMode()
        }
        fun testChartData(): MutableList<DataEntry> {
            val data: MutableList<DataEntry> = ArrayList()
            data.add(CustomDataEntry("P1", 24, 23, -21))
            data.add(CustomDataEntry("P2", 21, 22, -22))
            data.add(CustomDataEntry("P3", 0.2, 21, 21))
            data.add(CustomDataEntry("P4", -23.1, 1, 11))
            data.add(CustomDataEntry("P5", -14.0, 4, 5))
            return data
        }
    }
}