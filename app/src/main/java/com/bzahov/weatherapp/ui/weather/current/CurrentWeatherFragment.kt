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
import com.bzahov.weatherapp.data.db.entity.CurrentWeatherEntry
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
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.current_weather_fragment, container, false)

        view.textView_feels_like_temperature.setOnClickListener{
            bindUI(viewModel.location)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(CurrentWeatherViewModel::class.java)
        bindUI(viewModel.location)
    }

    private fun bindUI(location: String): Job {

        return launch {
            val currentWeatherLiveData = viewModel.weather.await()
            currentWeatherLiveData.observe(viewLifecycleOwner, Observer {
                if (it == null) return@Observer
                updateUI(it, location)
                Log.d(TAG, "Update UI with that data: $it")
            })
        }
    }

    private fun updateUI(
        it: CurrentWeatherEntry,
        location: String
    ) {
        group_loading.visibility = View.GONE
        updateCondition(it.weatherDescriptions.toString())
        updateDateToToday()
        updateLocation(location) //REWORK show location which is choosen in settings not location of showing location
        updatePrecipitation(it.precipation)
        updateTemperatures(it.temperature, it.feelslike)
        updateWind(it.dir, it.speed)
        updateVisibility(it.visibility)
        updateBackground(it)
        GlideApp.with(this)
            .load(it.weatherIcons.last())
            .into(imageView_condition_icon)


    }

    private fun updateBackground(it: CurrentWeatherEntry) {
        if (it.isDay == "yes") {
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
        if (viewModel.isMetric) textView_temperature.text = "$temperature$unitAbbreviation"
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

    private fun refreshWeather(){

    }
}
