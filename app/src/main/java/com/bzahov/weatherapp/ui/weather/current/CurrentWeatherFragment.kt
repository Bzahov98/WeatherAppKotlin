package com.resocoder.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.WeatherApiService
import com.google.android.gms.common.api.Scope
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CurrentWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentWeatherFragment()
    }

    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)
        val apiService = WeatherApiService()
        val location = "Sofia"
        GlobalScope.launch(Dispatchers.Main) {
            val currentWeatherResponse = apiService
                .getCurrentWeather(location)
                .await()
            val TAG = "cwr"
            Log.d(TAG,"before currentWeather response ")

//            currentWeatherResponse.runCatching {
                currentWeather.text = currentWeatherResponse.currentWeatherEntry.toString()
                Log.d(TAG,"currentWeather response $currentWeatherResponse")
//            }
            //currentWeather.text = currentWeatherResponse.currentWeatherEntry.toString()
        }
    }

}
