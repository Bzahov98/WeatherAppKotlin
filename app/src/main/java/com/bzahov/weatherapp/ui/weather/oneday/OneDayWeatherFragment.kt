package com.bzahov.weatherapp.ui.weather.oneday

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.provider.TAG
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateIcon
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateLocation
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.weather.oneday.recyclerview.HourInfoItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_one_day.*
import kotlinx.android.synthetic.main.item_one_day.view.*
import kotlinx.android.synthetic.main.layout_day_night_description_view.view.*
import kotlinx.android.synthetic.main.layout_temperature_view.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class OneDayWeatherFragment : ScopedFragment(), KodeinAware {

    private lateinit var allWeatherData: List<FutureDayData>
    private lateinit var allDayWeatherData: List<FutureDayData>
    private lateinit var allNightweatherData: List<FutureDayData>

    private lateinit var viewModel: OneDayWeatherViewModel
    override val kodein by closestKodein()
    private val viewModelFactory: OneDayWeatherViewModelFactory by instance<OneDayWeatherViewModelFactory>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.item_one_day, container, false)
        return inflate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(OneDayWeatherViewModel::class.java)
        viewModel.resetStartEndDates()
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        bindUI()
    }

    private fun bindUI(): Job {
        return launch {
            val oneDayWeatherLiveData = viewModel.weather.await()
            val weatherLocation = viewModel.weatherLocation.await()

            Log.d(TAG, "buildUi $oneDayWeatherLiveData")
            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
                updateLocation(location.name, requireActivity())
                Log.d(TAG, "bindUI Update location with that data: $location")
            })

            oneDayWeatherLiveData.observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "UpdateUI for List<FutureDayData> with: \n ${it ?: "null"} \n")
                if (it == null) return@Observer

                allWeatherData = it
                /*if (allWeatherDataForCurrentDay.isEmpty()) {
                     launch {
                        viewModel.requestRefreshOfData()
                    }
                    return@Observer
                }*/
                if (allWeatherData.isNullOrEmpty()) {
                    Log.e(TAG, "DATA IS EMPTY TRY TO FETCH AGAIN")
                    launch {
                        viewModel.requestRefreshOfData()
                    }
                    return@Observer
                }

                updateUI(it)
                initRecyclerView(it.toHourInfoItems())
            })
        }
    }

    private fun initRecyclerView(toHourInfoItems: List<HourInfoItem>) {
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            this.addAll(toHourInfoItems)
        }
        oneDayPerHourRecyclerView.apply {
            adapter = groupAdapter
        }
        groupAdapter.setOnItemClickListener { item, view ->
            val itemDetail = (item as HourInfoItem).weatherEntry
            Toast.makeText(
                this@OneDayWeatherFragment.context,
                "Clicked: ${itemDetail.dtTxt}",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, "\n\nGroupAdapter.setOnItemClickListener ${itemDetail}\n")
            showHourInfoDetails(itemDetail.dtTxt, view)
        }
    }

    private fun showHourInfoDetails(dtTxt: String, view: View) {
        val dtFormatter =
            DateTimeFormatter.ofPattern(view.context.getString(R.string.date_formatter_pattern))
        //val dateString = dateTime.format(dtFormatter)
        val actionShowDetail =
            OneDayWeatherFragmentDirections.actionShowDetail(dtTxt)// .onNestedPrePerformAccessibilityAction(view,)//.action(dateString)
        Navigation.findNavController(view).navigate(actionShowDetail)
    }

    private fun List<FutureDayData>.toHourInfoItems(): List<HourInfoItem> {
        val dayForShow =
            LocalDate.now().dayOfMonth.toString()
        val monthForShow =
            LocalDate.now().month.toString()
        Log.d(
            TAG,
            "random dt_text: ${this.last().dtTxt} and contains phase: >>$dayForShow:$monthForShow<</n"
        )
        ///allWeatherDataForCurrentDay = this.filter { it.dtTxt.contains("$dayForShow:$monthForShow") }
        if (viewModel.hasUnitSystemChanged()) {
            /*launch {
                viewModel.requestRefreshOfData()
                viewModel.unitProvider.notifyNoNeedToChangeUnitSystem()
            }*/
        }
        return allWeatherData.map {
            HourInfoItem(it, viewModel.isMetric)
        }.apply { }
    }

    private fun updateUI(it: List<FutureDayData>) {
        oneDayGroupLoading.visibility = View.GONE
        updateActionBarDescription()
        updateDayTemperatures(requireView().oneDayTemperatureView)
        updateNightTemperatures(requireView().oneNightTemperatureView)
        updateConditionIconsView(requireView().oneDayNightIconDescrView)

    }

    private fun updateConditionIconsView(view: View) {
        val dayWeatherDetailsLast = allDayWeatherData.first().weatherDetails.first()
        val nightWeatherDetailsLast = allNightweatherData.first().weatherDetails.first()
        view.iconDayViewConditionText.text = dayWeatherDetailsLast.description
        view.iconNightViewConditionText.text = dayWeatherDetailsLast.description
        val nightIconNumber = dayWeatherDetailsLast.icon
        val dayIconNumber = nightWeatherDetailsLast.icon
        updateIcon(nightIconNumber, view.iconViewNightConditionIcon)
        updateIcon(dayIconNumber, view.iconViewDayConditionIcon)
    }


    private fun updateDayTemperatures(view: View) {
        allDayWeatherData = allWeatherData.filter(isDayTime())

        view.tempViewTittleText.text =
            ForecastApplication.getAppString(R.string.tempView_title_day)
        fillTempViews(allDayWeatherData, view)
    }

    private fun updateNightTemperatures(view: View) {
        allNightweatherData = allWeatherData.filterNot(isDayTime())
        view.tempViewTittleText.text =
            ForecastApplication.getAppString(R.string.tempView_title_night)

        fillTempViews(allNightweatherData, view)
    }

    private fun fillTempViews(data: List<FutureDayData>, view: View) {
        val unitAbbreviation = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
            viewModel.isMetric,
            getString(R.string.metric_temperature),
            getString(R.string.imperial_temperature)
        )
        val calculatedData: MinMaxAvgTemp = calculateMinMaxAvgTemp(data)
        view.tempViewMaxTemp.text = calculatedData.maxTemp.toString()
        view.tempViewMinTemp.text = calculatedData.minTemp.toString()
        view.tempViewTemperature.text =
            String.format("%.1f $unitAbbreviation", calculatedData.calculateAvrTemp())
        view.tempViewFeelsLike.text =
            String.format("%.1f $unitAbbreviation", calculatedData.calculateAvrFeelsTemp())
    }

    private fun calculateMinMaxAvgTemp(data: List<FutureDayData>): MinMaxAvgTemp {
        val result = MinMaxAvgTemp()
        data.forEach {
            result.avgCount++
            result.avgSumTemp += it.main.temp
            result.avgSumFeelsTemp += it.main.feelsLike

            if (result.maxTemp < it.main.temp) {
                result.maxTemp = it.main.temp
            }
            if (result.minTemp > it.main.temp) {
                result.minTemp = it.main.temp
            }
        }
        return result
    }

    private fun isDayTime(): (FutureDayData) -> Boolean {
        return {
            // REWORK Fix Offset
            val currentDay = LocalDateTime.ofEpochSecond(it.dt, 0, ZoneOffset.ofTotalSeconds(0))
            currentDay.isAfter(currentDay.withHour(8)).and(
                currentDay.isBefore(currentDay.withHour(20))
            )
        }
    }


    private fun updateActionBarDescription() {

        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            UIConverterFieldUtils.dateTimestampToDateString(allWeatherData[0].dt)
    }

    override fun onResume() {
        super.onResume()
        bindUI()
    }

    private fun updateDate() {
        /*oneDayWeatherDate.text =
            UIConverterFieldUtils.dateTimestampToDateString(allWeatherDataForCurrentDay.get(1).dt)
*/
    }

}

class MinMaxAvgTemp() {

    var minTemp: Double = 100.0
    var maxTemp: Double = 0.0
    var avgSumTemp: Double = 0.0
    var avgSumFeelsTemp: Double = 0.0
    var avgCount: Int = 0
    fun calculateAvrTemp(): Double {
        if (avgCount == 0) {
            return 0.0
        }
        return avgSumTemp / avgCount
    }

    fun calculateAvrFeelsTemp(): Double {
        if (avgCount == 0) {
            return 0.0
        }
        return avgSumFeelsTemp / avgCount
    }

}

