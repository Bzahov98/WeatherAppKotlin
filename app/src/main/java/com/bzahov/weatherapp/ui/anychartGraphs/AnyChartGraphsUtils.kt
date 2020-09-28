package com.bzahov.weatherapp.ui.anychartGraphs

import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.core.cartesian.series.Base
import com.anychart.enums.Anchor

class AnyChartGraphsUtils {
    companion object{
        fun com.anychart.core.axes.Linear.setAxisLabelName(unitAbbreviation : String){
            this.labels().format("{%Value} $unitAbbreviation")
        }

        fun colorizeAxesBackground(axis : com.anychart.core.axes.Linear) {
            var xLabelsBackground = axis.labels().background();
            xLabelsBackground.enabled(true);
            xLabelsBackground.stroke("#cecece");
            xLabelsBackground.cornerType("round");
            xLabelsBackground.corners(5);
        }

        fun setSeriesAnchor(series: Base, anchor: Anchor, title: String? = null) {
            var tooltip1 = series.tooltip();
            tooltip1.anchor(anchor);
            if (title == null) {
                tooltip1.title(false);
            } else tooltip1.title(title);

            tooltip1.separator(false);
        }

        fun testChartData(): MutableList<DataEntry> {
            val data: MutableList<DataEntry> = ArrayList()
            data.add(OneDayChartUtils.Companion.CustomDataEntry("P1", 24, 23, -21))
            data.add(OneDayChartUtils.Companion.CustomDataEntry("P2", 21, 22, -22))
            data.add(OneDayChartUtils.Companion.CustomDataEntry("P3", 0.2, 21, 21))
            data.add(OneDayChartUtils.Companion.CustomDataEntry("P4", -23.1, 1, 11))
            data.add(OneDayChartUtils.Companion.CustomDataEntry("P5", -14.0, 4, 5))
            return data
        }
    }
}