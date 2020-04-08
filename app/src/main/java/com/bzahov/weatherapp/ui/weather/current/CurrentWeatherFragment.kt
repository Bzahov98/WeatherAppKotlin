package com.resocoder.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.WeatherApiService
import com.bzahov.weatherapp.data.network.ConnectivityInterceptorImpl
import com.bzahov.weatherapp.data.network.WeatherNetworkDataSourceImpl
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
        val apiService = WeatherApiService(ConnectivityInterceptorImpl(this.context!! ))
        val weatherNetworkDataSource = WeatherNetworkDataSourceImpl(apiService)
        weatherNetworkDataSource.downloadedCurrentWeather.observe(this.viewLifecycleOwner, Observer {
            currentWeather.text = it.toString().replace(' ', '\n')
        })

        val location = "Sofia"
        val language = "en"
        val unit = "m"
        GlobalScope.launch(Dispatchers.Main) {
            weatherNetworkDataSource.fetchCurrentWeather(location,/*language,*/unit)
            /*val currentWeatherResponse = apiService
                .getCurrentWeather(location)
                .await()
            val TAG = "cwr"
            Log.d(TAG,"before currentWeather response ")*/
            /*currentWeatherResponse.runCatching {
                val result = currentWeatherResponse.toString().replace(' ', '\n')
                currentWeather.text = result
                Log.d(TAG,"currentWeather response $result")
            }*/
        }
    }

}
