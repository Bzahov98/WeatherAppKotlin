package com.resocoder.forecastmvvm.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.WeatherApiService
import com.bzahov.weatherapp.data.network.ConnectivityInterceptorImpl
import com.bzahov.weatherapp.data.network.WeatherNetworkDataSourceImpl
import com.bzahov.weatherapp.ui.base.ScopedFragment
import com.bzahov.weatherapp.ui.weather.current.CurrentWeatherViewModelFactory
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CurrentWeatherFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
//    companion object {
//        fun newInstance() = CurrentWeatherFragment()
//    }

    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(CurrentWeatherViewModel::class.java)
        bindUI()
        //deprecatedRequest()
    }

    private fun bindUI() = GlobalScope.launch {
        val currentWeatherLiveData = viewModel.weather.await()
        currentWeatherLiveData.observe(this@CurrentWeatherFragment.viewLifecycleOwner, Observer {
            currentWeather.text = it?.toString()
        })
    }


    @Deprecated("old request to api")
    private fun deprecatedRequestToApi() {
        val apiService = WeatherApiService(ConnectivityInterceptorImpl(this.context!!))
        val weatherNetworkDataSource = WeatherNetworkDataSourceImpl(apiService)

        weatherNetworkDataSource.downloadedCurrentWeather.observe(
            this.viewLifecycleOwner,
            Observer {
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
