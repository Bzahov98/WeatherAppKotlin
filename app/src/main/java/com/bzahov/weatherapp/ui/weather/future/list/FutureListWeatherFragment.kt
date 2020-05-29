package com.bzahov.weatherapp.ui.weather.future.list


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitleWithResource
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateLocation
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.weather.future.list.recyclerview.FutureWeatherItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.future_list_weather_fragment.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FutureListWeatherFragment : ScopedFragment(), KodeinAware {
    private lateinit var allWeatherData: List<FutureDayData>
    private val TAG = "FutureDetailWeatherFragment"
    override val kodein by closestKodein()
    private val viewModelFactory: FutureListWeatherViewModelFactory by instance<FutureListWeatherViewModelFactory>()
    private lateinit var viewModel: FutureListWeatherViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.future_list_weather_fragment, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(FutureListWeatherViewModel::class.java)
        bindUI()

    }

    private fun bindUI(): Job {
        Log.d(TAG, "bindUI")
        return launch {
            val futureWeatherLiveData = viewModel.forecastWeather.await()
            val weatherLocation = viewModel.weatherLocation.await()

            Log.d(TAG, "buildUi $futureWeatherLiveData")
            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
                updateLocation(location.name, requireActivity())
                Log.d(TAG, "bindUI Update location with that data: $location")
            })

            futureWeatherLiveData.observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "UpdateUI for List<FutureDayData> with: \n ${it ?: "null"} \n")
                if (it.isNullOrEmpty()) {
                    Log.e(TAG, "DATA IS EMPTY TRY TO FETCH AGAIN")
                    launch {
                        viewModel.requestRefreshOfData()
                    }
                    return@Observer
                } else {
                    updateUI()
                    initRecyclerView(it.toFutureWeatherItems())
                }
            })
        }
    }

    private fun initRecyclerView(items: List<FutureWeatherItem>) {
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            this.addAll(items)
        }
        futureRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FutureListWeatherFragment.context)
            adapter = groupAdapter
        }

        groupAdapter.setOnItemClickListener { item, view ->
            Toast.makeText(
                this@FutureListWeatherFragment.context,
                "Clicked: ${item.itemCount}",
                Toast.LENGTH_SHORT
            ).show()

            val itemDetail = (item as FutureWeatherItem).weatherEntry
            Log.e(TAG, "\n\nGroupAdapter.setOnItemClickListener ${itemDetail}\n")
            showWeatherDetail(itemDetail.dtTxt, view)
        }
    }

    private fun showWeatherDetail(string: String, view: View) {
        val actionShowDetail = FutureListWeatherFragmentDirections.actionShowDetail(string)
        Navigation.findNavController(view).navigate(actionShowDetail)
    }

    private fun List<FutureDayData>.toFutureWeatherItems(): List<FutureWeatherItem> {
        return this.filter { it.dtTxt.contains(getString(R.string.default_future_time_calibration)) }
            .map { FutureWeatherItem(it, viewModel.isMetric) }
            .apply { }
    }

    private fun updateUI() {
        futureGroupLoading.visibility = View.GONE
        updateActionBarSubtitleWithResource(
            R.string.future_weather_five_days_next,
            requireActivity()
        );
    }

    override fun onResume() {
        super.onResume()
        bindUI()
    }
}
