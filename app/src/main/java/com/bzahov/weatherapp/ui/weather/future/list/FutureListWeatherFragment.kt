package com.bzahov.weatherapp.ui.weather.future.list


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.future_list_weather_fragment.*
import kotlinx.android.synthetic.main.future_list_weather_fragment.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FutureListWeatherFragment : ScopedFragment(), KodeinAware {
    private val TAG = "FutureDetailWeatherFragment"
    override val kodein by closestKodein()
    private val viewModelFactory: FutureListWeatherViewModelFactory by instance<FutureListWeatherViewModelFactory>()
    private lateinit var viewModel: FutureListWeatherViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.future_list_weather_fragment, container, false)
        view.list_item.setOnClickListener{
            val bla = "sa" + list_item.text
            list_item.text = bla
            bindUI()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(FutureListWeatherViewModel::class.java)
        bindUI()

    }

    private fun bindUI(): Job {
        Log.d(TAG,"blal")
        return launch {
            val futureWeatherLiveData = viewModel.forecastWeather.await()
            val weatherLocation = viewModel.weatherLocation.await()

            Log.d(TAG, "buildUi ${futureWeatherLiveData.toString()}")
            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
                //updateLocation(location.name)
                Log.d(TAG, "Update location with that data: $location")
            })
            futureWeatherLiveData.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                list_item?.text = it.toString()
                Log.d(TAG, "Update location with that data: $it")
            })
        }
    }
}
