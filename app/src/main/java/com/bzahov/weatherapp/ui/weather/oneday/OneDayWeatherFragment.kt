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
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
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
//    private lateinit var allWeatherData: List<FutureDayData>
//    private lateinit var allDayWeatherData: List<FutureDayData>
//    private lateinit var allNightWeatherData: List<FutureDayData>

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
            viewModel.getOneDayData()
            val oneDayWeatherLiveData = viewModel.uiViewsState
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

                /*allWeatherData = it
                if (allWeatherData.isNullOrEmpty()) {
                    Log.e(TAG, "DATA IS EMPTY TRY TO FETCH AGAIN")
                    launch {
                        viewModel.requestRefreshOfData()
                    }
                    return@Observer
                } else {
*/
                updateUI(it)
                initRecyclerView(it.hourInfoItemsList)
//                }
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

    private fun updateUI(it: OneDayWeatherState) {
        oneDayGroupLoading.visibility = View.GONE
        updateDayTemperatures(it.allDayWeatherAndAverageData, requireView().oneDayTemperatureView)
        updateNightTemperatures(
            it.allNightWeatherAndAverageData,
            requireView().oneNightTemperatureView
        )
        updateActionBarDescription(it.oneDaySubtitle)
        updateConditionIconsView(
            it.allDayWeatherAndAverageData,
            it.allNightWeatherAndAverageData,
            requireView().oneDayNightIconDescrView
        )

    }

    private fun updateConditionIconsView(
        allDayWeatherAndAverageData: MinMaxAvgTemp,
        allNightWeatherAndAverageData: MinMaxAvgTemp,
        view: View
    ) {
        // Day view
        val dayIconNumber = allDayWeatherAndAverageData.iconConditionID
        view.iconDayViewConditionText.text = allDayWeatherAndAverageData.iconViewConditionText
        updateIcon(dayIconNumber, view.iconViewDayConditionIcon)

        // Night view
        val nightIconNumber = allNightWeatherAndAverageData.iconConditionID
        view.iconNightViewConditionText.text = allNightWeatherAndAverageData.iconViewConditionText
        updateIcon(nightIconNumber, view.iconViewNightConditionIcon)

    }


    private fun updateDayTemperatures(calculatedDayData: MinMaxAvgTemp, view: View) {
        view.tempViewTittleText.text = getAppString(R.string.tempView_title_day)
        fillTempViews(calculatedDayData, view.oneDayTemperatureView)
    }

    private fun updateNightTemperatures(calculatedNightData: MinMaxAvgTemp, view: View) {
        view.tempViewTittleText.text = getAppString(R.string.tempView_title_night)
        fillTempViews(calculatedNightData, view.oneNightTemperatureView)
    }

    private fun fillTempViews(calculatedData: MinMaxAvgTemp, view: View) {

        view.tempViewMaxTemp.text = calculatedData.maxTempText
        view.tempViewMinTemp.text = calculatedData.minTempText
        view.tempViewTemperature.text = calculatedData.averageTempText
        view.tempViewFeelsLike.text = calculatedData.averageFeelLikeTempText

    }

    private fun updateActionBarDescription(subtitle: String) {

        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = subtitle
    }

    override fun onResume() {
        super.onResume()
        bindUI()
    }
}



