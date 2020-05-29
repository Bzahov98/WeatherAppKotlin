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
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.internal.OtherUtils.Companion.isDayTime
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

class OneDayWeatherFragment : ScopedFragment(), KodeinAware {
    private val TAG = "OneDayWeatherFragment"
    private lateinit var allWeatherData: List<FutureDayData>
    private lateinit var allDayWeatherData: List<FutureDayData>
    private lateinit var allNightWeatherData: List<FutureDayData>

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
                if (allWeatherData.isNullOrEmpty()) {
                    Log.e(TAG, "DATA IS EMPTY TRY TO FETCH AGAIN")
                    launch {
                        viewModel.requestRefreshOfData()
                    }
                    return@Observer
                } else {

                    updateUI()
                    initRecyclerView(toHourInfoItems())
                }
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
            Log.e(TAG, "GroupAdapter.setOnItemClickListener ${itemDetail}\n")
            showHourInfoDetails(itemDetail.dtTxt, view)
        }
    }

    private fun showHourInfoDetails(dtTxt: String, view: View) {
        val actionShowDetail =
            OneDayWeatherFragmentDirections.actionShowDetail(dtTxt)
        Navigation.findNavController(view).navigate(actionShowDetail)
    }

    private fun toHourInfoItems(): List<HourInfoItem> {
        return allWeatherData.map {
            HourInfoItem(it, viewModel.isMetric, viewModel.getTimeZoneOffsetInSeconds())
        }.apply { }
    }

    private fun updateUI() {
        oneDayGroupLoading.visibility = View.GONE
        updateDayTemperatures(requireView().oneDayTemperatureView)
        updateNightTemperatures(requireView().oneNightTemperatureView)
        updateActionBarDescription()
        updateConditionIconsView(requireView().oneDayNightIconDescrView)

    }

    private fun updateConditionIconsView(view: View) {
        if (allDayWeatherData.isNotEmpty()) {
            val dayWeatherDetailsLast = allDayWeatherData.random().weatherDetails.random()
            view.iconDayViewConditionText.text = dayWeatherDetailsLast.description

            val dayIconNumber = dayWeatherDetailsLast.icon
            updateIcon(dayIconNumber, view.iconViewDayConditionIcon)
        } else {
            Log.e(TAG, "allDayWeatherData is EMPTY")
        }
        if (allNightWeatherData.isNotEmpty()) {
            val nightWeatherDetailsLast = allNightWeatherData.last().weatherDetails.random()
            val nightIconNumber = nightWeatherDetailsLast.icon

            view.iconNightViewConditionText.text = nightWeatherDetailsLast.description
            updateIcon(nightIconNumber, view.iconViewNightConditionIcon)

        } else {
            Log.e(TAG, "allNightWeatherData is EMPTY")
            view.iconNightViewConditionText.text = getAppString(R.string.error_no_info)
        }
    }


    private fun updateDayTemperatures(view: View) {
        allDayWeatherData = allWeatherData.filter(isDayTime(viewModel.getTimeZoneOffsetInSeconds()))

        view.tempViewTittleText.text =
            ForecastApplication.getAppString(R.string.tempView_title_day)
        fillTempViews(allDayWeatherData, view)
    }

    private fun updateNightTemperatures(view: View) {
        allNightWeatherData =
            allWeatherData.filterNot(isDayTime(viewModel.getTimeZoneOffsetInSeconds()))
        view.tempViewTittleText.text =
            ForecastApplication.getAppString(R.string.tempView_title_night)

        fillTempViews(allNightWeatherData, view)
    }

    private fun fillTempViews(data: List<FutureDayData>, view: View) {
        val unitAbbreviation = UIConverterFieldUtils.chooseLocalizedUnitAbbreviation(
            viewModel.isMetric,
            getAppString(R.string.metric_temperature),
            getAppString(R.string.imperial_temperature)
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

    private fun updateActionBarDescription() {

        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            UIConverterFieldUtils.dateTimestampToDateString(
                allWeatherData[0].dt,
                viewModel.getTimeZoneOffsetInSeconds()
            )
    }

    override fun onResume() {
        super.onResume()
        bindUI()
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

