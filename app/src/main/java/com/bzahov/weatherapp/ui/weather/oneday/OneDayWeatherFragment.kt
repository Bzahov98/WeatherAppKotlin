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
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.provider.TAG
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.weather.oneday.recyclerview.HourInfoItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_one_day.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.time.LocalDate

class OneDayWeatherFragment : ScopedFragment(), KodeinAware {

    private lateinit var allWeatherDataForCurrentDay: List<FutureDayData>
    private lateinit var viewModel: OneDayWeatherViewModel
    override val kodein by closestKodein()
    private val viewModelFactory: OneDayWeatherViewModelFactory by instance<OneDayWeatherViewModelFactory>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_one_day, container, false)
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
            val futureWeatherLiveData = viewModel.weather.await()
            val weatherLocation = viewModel.weatherLocation.await()

            Log.d(TAG, "buildUi $futureWeatherLiveData")
            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
                updateLocation(location.name)
                Log.d(TAG, "bindUI Update location with that data: $location")
            })

            futureWeatherLiveData.observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "UpdateUI for List<FutureDayData> with: \n ${it ?: "null"} \n")
                if (it == null) return@Observer

                allWeatherDataForCurrentDay = it
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
            Toast.makeText(this@OneDayWeatherFragment.context, "Clicked: ${item.itemCount}", Toast.LENGTH_SHORT).show()

            val itemDetail = (item as HourInfoItem).weatherEntry
            Log.e(TAG,"\n\nGroupAdapter.setOnItemClickListener ${itemDetail}\n")
            showHourInfoDetails(itemDetail.dtTxt, view)
        }
    }

    private fun showHourInfoDetails(dtTxt: String, view: View) {
       // TODO"Not yet implemented")
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

        return allWeatherDataForCurrentDay.map {
            HourInfoItem(it, viewModel.isMetric)
        }.apply { }
    }

    private fun updateUI(it: List<FutureDayData>) {
        // oneDayGroupLoading.visibility = View.GONE
        updateDate();
        updateActionBarDescription()
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateActionBarDescription() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            UIConverterFieldUtils.dateTimestampToDateString(allWeatherDataForCurrentDay.first().dt)
    }

    override fun onResume() {
        super.onResume()
        bindUI()
    }

    private fun updateDate() {
        oneDayWeatherDate.text =
            UIConverterFieldUtils.dateTimestampToDateString(allWeatherDataForCurrentDay.first().dt)
    }
}

