package com.bzahov.weatherapp.ui.weather.current

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anychart.APIlib
import com.anychart.AnyChartView
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.internal.UIUpdateViewUtils
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitleWithResource
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarTitle
import com.bzahov.weatherapp.internal.glide.GlideApp
import com.bzahov.weatherapp.ui.anychartGraphs.AnyChartGraphsFactory.Companion.initChart
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.CurrentWeatherChartUtils
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.CurrentWeatherChartUtils.Companion.drawPercentRadarChart
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.CurrentWeatherChartUtils.Companion.drawThermometer
import com.bzahov.weatherapp.ui.anychartGraphs.specificUtils.CurrentWeatherChartUtils.Companion.drawWindGauge
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.base.states.EmptyState
import com.bzahov.weatherapp.ui.remoteviews.widgets.interfaces.CurrentWidgetRefresher
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
            .circleCrop()
            .into(currentIConditionIcon)

        initThermometerChart(it, currentThermometerChartView)
        initWindChart(it, currentWindChartView)
        initRadarChart(it, currentRadarChartView)
    }

    private fun initThermometerChart(it: CurrentWeatherState, view: AnyChartView?) {
        if (view == null) {
            Log.e(CurrentWeatherChartUtils.TAG, " currentThermometerChartView is null")
            return
        }
//        APIlib.getInstance().setActiveAnyChartView(currentThermometerChartView)
//        currentThermometerChartView.setDebug(true)
        initChart(it, view) { drawThermometer(it) }
        //currentThermometerChartView.setChart(drawThermometer(it))
    }

    //                drawingChart: ((OneDayWeatherState?)-> Cartesian)
//            chartView.setChart(drawingChart.invoke(weatherStateData))
    private fun initWindChart(it: CurrentWeatherState, view: AnyChartView?) {
        if (view == null) {
            Log.e(CurrentWeatherChartUtils.TAG, " currentThermometerChartView is null")
            return
        }
//        APIlib.getInstance().setActiveAnyChartView(view)
//        view.setDebug(true)
//
//        view.setChart(drawWindGauge(it))
        initChart(it, view) { drawWindGauge(it) }
    }
private fun initRadarChart(it: CurrentWeatherState, view: AnyChartView?){
    if (view != null) {
        initChart(it, view) { drawPercentRadarChart(it) }
    }
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
                Log.e(TAG, getAppString(R.string.warning_device_offline))
                UIUpdateViewUtils.showSnackBarMessage(
                    getAppString(R.string.warning_device_offline),
                    requireActivity(),
                    false
                )
            }
        }
    }

    //TODO Rework change background with proper color to match to all pictures background
    private fun updateBackground(isDay: Boolean) {
//        if (isDay) {
//            val dayColor = ContextCompat.getColor(
//                requireContext(),
//                R.color.colorWeatherIconBackgroundDay
//            )
//            currentWeatherFragment.setBackgroundColor(
//                dayColor
//            )
//
//            setChartBackground(dayColor,currentChartView)
//
//        } else {
//            val nightColor = ContextCompat.getColor(
//                requireContext(),
//                R.color.colorWeatherIconBackgroundNight
//            )
//            currentWeatherFragment.setBackgroundColor(
//                nightColor
//            )
//            setChartBackground(nightColor,currentChartView)
//        }
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
