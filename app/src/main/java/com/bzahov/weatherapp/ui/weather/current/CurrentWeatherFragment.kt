package com.bzahov.weatherapp.ui.weather.current

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.internal.glide.GlideApp
import com.bzahov.weatherapp.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.android.synthetic.main.current_weather_fragment.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class CurrentWeatherFragment : ScopedFragment(), KodeinAware {
    private val TAG = "CurrentWeatherFragment"
    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance<CurrentWeatherViewModelFactory>()
    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.current_weather_fragment, container, false)

        view.textView_feels_like_temperature.setOnClickListener {
            launch {   refreshWeather() }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(CurrentWeatherViewModel::class.java)
        bindUI()
    }

    private fun bindUI(): Job {
        return launch {
            val currentWeatherLiveData = viewModel.weather.await()
            val weatherLocation = viewModel.weatherLocation.await()
            // FIX: current location isnt changed and stay with name current_weather_fragment
            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
                Log.e(TAG,"Location : $location")
                updateLocation(location.city.name)
                Log.d(TAG, "Update location with that data: $location")
            })

            currentWeatherLiveData.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                updateUI(it)
                Log.d(TAG, "Update UI with that data: $it")
            })
        }
    }

    private fun updateUI(it: CurrentWeatherEntry) {
        group_loading.visibility = View.GONE
        updateCondition(it.weatherDescriptions.toString())
        updateDateToToday()
        updatePrecipitation(it.precipation)
        updateTemperatures(it.temperature, it.feelslike)
        updateWind(it.dir, it.speed)
        updateVisibility(it.visibility)
        updateBackground(it)
        GlideApp.with(this)
            .load(it.weatherIcons.last())
            .into(imageView_condition_icon)
    }

    // Rework change background with proper color to match to all pictures background
    private fun updateBackground(it: CurrentWeatherEntry) {
        if (it.isDay == getString(R.string.is_day_yes)) {
            currentWeatherFragment.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorWeatherIconBackgroundDay
                )
            )
        } else {
            currentWeatherFragment.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.colorWeatherIconBackgroundNight
                )
            )
        }
    }

    // FIX : current location isnt changed and stay with name current_weather_fragment
    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDateToToday() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            getString(R.string.current_weather_today)
    }

    private fun updateTemperatures(temperature: Double, feelsLike: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            getString(R.string.metric_temperature),
            getString(R.string.imperial_temperature)
        )
        textView_temperature.text = "$temperature$unitAbbreviation"
        textView_feels_like_temperature.text = "Feels like $feelsLike$unitAbbreviation"
    }

    private fun updateCondition(condition: String) {
        textView_condition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            getString(R.string.metric_precipitation),
            getString(R.string.imperial_precipitation)
        )
        textView_precipitation.text = "Precipitation: $precipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            getString(R.string.metric_speed),
            getString(R.string.imperial_speed)
        )
        textView_wind.text = "Wind: $windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            getString(R.string.metric_distance),
            getString(R.string.imperial_distance)
        )
        textView_visibility.text = "Visibility: $visibilityDistance $unitAbbreviation"
    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial
    }

    private suspend fun refreshWeather() {
        viewModel.requestRefreshOfData()
    }
}
