package com.bzahov.weatherapp.ui.weather.current

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.current.CurrentWeatherEntry
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitleWithResource
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateLocation
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

        view.currentFeelsLikeTemperature.setOnClickListener {
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
            // REWORK Return City object temporary FIX for: current location isn't changed and stay with name current_weather_fragment all city attributes are null

            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
                Log.e(TAG,"Location : $location")
                updateLocation(location.name, requireActivity())
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
        currentGroupLoading.visibility = View.GONE
        updateCondition(it.weatherDescriptions.toString())
        updateActionBarSubtitleWithResource(R.string.current_weather_today,requireActivity())
        updatePrecipitation(it.precipation)
        updateTemperatures(it.temperature, it.feelslike)
        updateWind(it.dir, it.speed)
        updateVisibility(it.visibility)
        updateBackground(it)
        GlideApp.with(this)
            .load(it.weatherIcons.last())
            .into(currentIConditionIcon)
    }

    // Rework change background with proper color to match to all pictures background
    private fun updateBackground(it: CurrentWeatherEntry) {
        if (it.isDay == getString(R.string.weather_stack_is_day)) {
            currentWeatherFragment.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorWeatherIconBackgroundDay
                )
            )
        } else {
            currentWeatherFragment.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorWeatherIconBackgroundNight
                )
            )
        }
    }

    private fun updateTemperatures(temp: Double, feelsLike: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            viewModel.isMetric,
            getString(R.string.metric_temperature),
            getString(R.string.imperial_temperature)
        )
        currentTextTemperature.text = "$temp$unitAbbreviation"
        currentFeelsLikeTemperature.text = getString(R.string.weather_text_feels_like) +" $feelsLike$unitAbbreviation"
    }

    private fun updateCondition(condition: String) {
        currentTextCondition.text = condition.removePrefix("[").removeSuffix("]")
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            viewModel.isMetric,
            getString(R.string.metric_precipitation),
            getString(R.string.imperial_precipitation)
        )
        currentPrecipitation.text = getString(R.string.weather_text_precipitation)+" $precipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            viewModel.isMetric,
            getString(R.string.metric_speed),
            getString(R.string.imperial_speed)
        )
        currentWind.text = getAppString(R.string.weather_text_wind)+"$windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(
            viewModel.isMetric,
            getString(R.string.metric_distance),
            getString(R.string.imperial_distance)
        )
        currentVisibility.text = getAppString(R.string.weather_text_visibility)+ "$visibilityDistance $unitAbbreviation"
    }

    private suspend fun refreshWeather() {
        viewModel.requestRefreshOfData()
    }
}
