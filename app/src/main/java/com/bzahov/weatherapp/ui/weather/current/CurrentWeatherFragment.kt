package com.bzahov.weatherapp.ui.weather.current

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.UIUpdateViewUtils
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitleWithResource
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarTitle
import com.bzahov.weatherapp.internal.glide.GlideApp
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.base.states.EmptyState
import com.bzahov.weatherapp.ui.remoteviews.widgets.interfaces.CurrentWidgetRefresher
import com.bzahov.weatherapp.ui.weather.oneday.OneDayDialogFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.android.synthetic.main.current_weather_fragment.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class CurrentWeatherFragment : ScopedFragment(), KodeinAware,
    CurrentWidgetRefresher {
    private val TAG = "CurrentWeatherFragment"
    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
    private lateinit var viewModel: CurrentWeatherViewModel
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.current_weather_fragment, container, false)
        //showSnackBarMessage("Swipe down to refresh data")

        mSwipeRefreshLayout = view.currentWeatherFragmentSwipe
        /*view.setOnTouchListener { v: View, m: MotionEvent ->
            // Perform tasks here
            when (m.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (isOnline(requireContext())) {
                        Log.d(TAG, "Updating Weather Data")
                        showSnackBarMessage("Updating Weather Data")
                        launch { refreshWeather() }
                        true
                    } else {
                        Log.e(TAG, "Your device is offline can't update data")
                        showSnackBarMessage("Your device is offline, can't update data", false)
                        true
                    }
                }
                else -> false
            }
        }*/

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefresherLayout()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(CurrentWeatherViewModel::class.java)
        bindUI()
    }

    private var location: String? = null

    private fun bindUI(): Job {
        return launch {
            viewModel.getCurrentWeather()
            val currentWeatherLiveData = viewModel.uiViewsState
            val weatherLocation = viewModel.weatherLocation.await()

            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer

                val sharedPref = getLocationSharedPreferences()
                sharedPref?.edit()
                    ?.putString(getString(R.string.preference_location_in_use_key), location.name)
                    ?.apply()
                updateActionBarTitle(location.name, requireActivity())
                Log.d(TAG, "Update location with that data: $location")
            })

            currentWeatherLiveData.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                requireContext().refreshWidgetData()
                when (it) {
                    is CurrentWeatherState -> {
                        Log.d(TAG, "Update UI with that data: $it")
                        updateUI(it)
                        updateNotificationData(it)
                    }
                    is EmptyState -> {
                        updateEmptyStateUI(it)
                        Log.e(TAG, "Update UI with EMPTY STATE $it")
                    }
                    else -> {
                        Log.e(TAG, "Found UNKNOWN STATE $it")
                    }
                }

            })
        }
    }

    //    override fun Context.updateCurrentWidget() {
//        val TAG = "ss"
//        Log.d(TAG,"request update of current weather widget")
//        val widgetUpdateIntent = Intent(this, CurrentWeatherWidget::class.java).apply {
//            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//            putExtra(
//                AppWidgetManager.EXTRA_APPWIDGET_IDS,
//                AppWidgetManager.getInstance(this@updateCurrentWidget).getAppWidgetIds(
//                    ComponentName(
//                        this@updateCurrentWidget,
//                        CurrentWeatherWidget::class.java
//                    )
//                )
//            )
//        }
//        sendBroadcast(widgetUpdateIntent)
//    }
    private fun updateNotificationData(it: CurrentWeatherState) {
        //val local = sharedPref?.getString(getString(R.string.preference_location_in_use_key))
        val titleIntent = Intent(getAppString(R.string.notification_action_title))
        titleIntent.putExtra("bla", getWeatherDescription(it))
//            getString(R.string.notification_action_title_weather_data_key),
//            arrayListOf(
//                getWeatherDescription(it),
//                it.currentCondition
//            )
//        )
        activity?.sendBroadcast(titleIntent);
    }

    private fun getWeatherDescription(it: CurrentWeatherState): String? {
        return "${getLocationString()} + ${it.currentFeelsLikeTemperature} | "
    }

    private fun getLocationString() =
        (getLocationSharedPreferences())?.getString(
            getString(R.string.preference_location_in_use_key),
            getAppString(R.string.default_location) + " erRor"
        )

    private fun getLocationSharedPreferences() =
        activity?.getSharedPreferences(
            getString(R.string.preference_current_location),
            Context.MODE_PRIVATE
        )

    private fun updateEmptyStateUI(it: EmptyState) {
        updateCondition(it.warningString)
        updateActionBarSubtitleWithResource(R.string.current_weather_today, requireActivity())
        updatePrecipitation(it.errorString)
        updateTemperatures(it.errorString, it.errorString)
        updateWind(it.errorString)
        updateVisibility(it.errorString)
        //updateBackground(it.isDay)
        // TODO put it into viewModel
        GlideApp.with(this)
            .load(it.errorIconID)
            .into(currentIConditionIcon)
    }


    private fun updateUI(it: CurrentWeatherState) {
        currentGroupLoading.visibility = View.GONE
        mSwipeRefreshLayout.isRefreshing = false

        updateCondition(it.currentCondition)
        updateActionBarSubtitleWithResource(R.string.current_weather_today, requireActivity())
        updatePrecipitation(it.currentPrecipitation)
        updateTemperatures(it.currentTemperature, it.currentFeelsLikeTemperature)
        updateWind(it.currentWind)
        updateVisibility(it.currentVisibility)
        updateBackground(it.isDay)
        // TODO put it into viewModel
        GlideApp.with(this)
            .load(it.iconStringID)
            .into(currentIConditionIcon)

        drawChart(it)
        drawThermometer(it)
    }

    private fun drawThermometer(it: CurrentWeatherState) {
        if (currentTermometerChartView == null) {
            // TODO add at landscape layout currentThermometerChartView
            Log.e(TAG, " currentThermometerChartView is null")
            return
        }
        APIlib.getInstance().setActiveAnyChartView(currentTermometerChartView)
        currentTermometerChartView.setDebug(true)

        val linearGauge = AnyChart.linear()

        // TODO data

        linearGauge.addPointer(SingleValueDataSet(arrayOf(it.weatherSingleData.temperature)))
        linearGauge.addPointer(SingleValueDataSet(arrayOf(it.weatherSingleData.feelslike)))
        linearGauge.addPointer(SingleValueDataSet(arrayOf(28)))
        linearGauge.addPointer(SingleValueDataSet(arrayOf(15)))
        //linearGauge.data())

        linearGauge.tooltip()
            .useHtml(true)
            .format(
                jsGetTermometerTooltip()
            )

        linearGauge.jsLabel(0,Position.LEFT_TOP,Anchor.LEFT_TOP, isMetric = true)
        linearGauge.jsLabel(1,Position.RIGHT_TOP,Anchor.RIGHT_TOP, isMetric = false)

        val scale: Base = linearGauge.scale()
            .minimum(-30)
            .maximum(40)
//                .setTicks

        //                .setTicks
        linearGauge.axis(0).scale(scale)
        linearGauge.axis(0)
            .offset("-1%")
            .width("0.5%")

        linearGauge.axis(0).labels()
            .format("{%Value}&deg;")
            .useHtml(true)

        linearGauge.thermometer(0)
            .name("Thermometer")
            .id(1)

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

        currentTermometerChartView.setChart(linearGauge)
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

    private fun jsGetTermometerTooltip(): String {
        return "function () {\n" +
                "          return this.value + '&deg;' + 'C' +\n" +
                "            ' (' + (this.value * 1.8 + 32).toFixed(1) +\n" +
                "            '&deg;' + 'F' + ')'\n" +
                "    }"
    }

    private fun drawChart(it: CurrentWeatherState) {
        if (currentChartView == null) {
            // TODO add at landscape layout currentChartView
            Log.e(TAG, " currentChartView is null ")
            return
        }
        APIlib.getInstance().setActiveAnyChartView(currentChartView)
        currentChartView.setDebug(true)

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

        currentChartView.setChart(cartesian)
    }


    private fun initRefresherLayout() {
        mSwipeRefreshLayout.isRefreshing = true
        mSwipeRefreshLayout.setOnRefreshListener {
            mSwipeRefreshLayout.isRefreshing = false
            if (viewModel.isOnline()) {
                Log.d(TAG, "Updating Weather Data")
                UIUpdateViewUtils.showSnackBarMessage("Updating Weather Data", requireActivity())
                launch { viewModel.requestRefreshOfData() }
            } else {
                Log.e(TAG, ForecastApplication.getAppString(R.string.warning_device_offline))
                UIUpdateViewUtils.showSnackBarMessage(
                    ForecastApplication.getAppString(R.string.warning_device_offline),
                    requireActivity(),
                    false
                )
            }
        }
    }

    //TODO Rework change background with proper color to match to all pictures background
    private fun updateBackground(isDay: Boolean) {
        if (isDay) {
            val dayColor = ContextCompat.getColor(
                requireContext(),
                R.color.colorWeatherIconBackgroundDay
            )
            currentWeatherFragment.setBackgroundColor(
                dayColor
            )

            setChartBackground(dayColor,currentChartView)

        } else {
            val nightColor = ContextCompat.getColor(
                requireContext(),
                R.color.colorWeatherIconBackgroundNight
            )
            currentWeatherFragment.setBackgroundColor(
                nightColor
            )
            setChartBackground(nightColor,currentChartView)
        }
    }

    private fun setChartBackground(
        dayColor: Int,
        chartView: AnyChartView?
    ) {
        if (chartView == null) {
            // TODO add at landscape layout currentChartView
            Log.e(TAG, " currentChartView is null ")
            return
        }
        APIlib.getInstance().setActiveAnyChartView(chartView)
        chartView.setBackgroundColor(dayColor)
        chartView.refreshDrawableState()
    }

    private fun updateTemperatures(temp: String, feelsLike: String) {
        currentTextTemperature.text = temp
        currentFeelsLikeTemperature.text = feelsLike
    }

    private fun updateCondition(condition: String) {
        currentTextCondition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: String) {
        currentPrecipitation.text = precipitationVolume
    }

    private fun updateWind(windText: String) {
        currentWind.text = windText
    }

    private fun updateVisibility(visibilityText: String) {
        currentVisibility.text = visibilityText
    }

    private suspend fun refreshWeather() {
        viewModel.requestRefreshOfData()
    }
}
