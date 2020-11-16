package com.bzahov.weatherapp.ui.weather.oneday

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.HoverMode
import com.anychart.enums.TooltipPositionMode
import com.anychart.scales.Linear
import com.bzahov.weatherapp.R
import kotlinx.android.synthetic.main.activity_graph_test.*
import kotlinx.android.synthetic.main.current_weather_fragment.*


class OneDayDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.activity_graph_test, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultView(any_chart_view)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       // val title = requireArguments().getInt("title")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.context)
        return builder
            .setMessage("Are you sure you want to reset the count?")
            .setNegativeButton("No",
                DialogInterface.OnClickListener { arg0, arg1 ->
                    Toast.makeText(
                        this@OneDayDialogFragment.context,
                        "Did not reset!",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            .setPositiveButton("Yes"
            ) { _, _ ->
                Toast.makeText(
                    this@OneDayDialogFragment.context,
                    "Did Reset!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .create()
    }

    companion object {
        fun newInstance(): OneDayDialogFragment {
            return OneDayDialogFragment()
        }
    }

    fun defaultView(anyChartView: AnyChartView) {
        APIlib.getInstance().setActiveAnyChartView(currentChartView)
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
}