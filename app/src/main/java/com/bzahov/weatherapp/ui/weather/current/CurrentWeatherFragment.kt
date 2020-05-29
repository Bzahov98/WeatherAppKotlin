package com.bzahov.weatherapp.ui.weather.current

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bzahov.weatherapp.R
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
            viewModel.getCurrentWeather()
            val currentWeatherLiveData = viewModel.uiViewsState
            val weatherLocation = viewModel.weatherLocation.await()

            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
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

    private fun updateUI(it: CurrentWeatherState) {
        currentGroupLoading.visibility = View.GONE
        updateCondition(it.currentCondition)
        updateActionBarSubtitleWithResource(R.string.current_weather_today,requireActivity())
        updatePrecipitation(it.currentPrecipitation)
        updateTemperatures(it.currentTemperature, it.currentFeelsLikeTemperature)
        updateWind(it.currentWind)
        updateVisibility(it.currentVisibility)
        updateBackground(it.isDay)
        // TODO put it into viewModel
        GlideApp.with(this)
            .load(it.weatherData.weatherIcons.last())
            .into(currentIConditionIcon)
    }

    // Rework change background with proper color to match to all pictures background
    private fun updateBackground(isDay: Boolean) {
        if (isDay) {
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

    private fun updateTemperatures(temp: String, feelsLike: String) {
        currentTextTemperature.text = temp
        currentFeelsLikeTemperature.text = feelsLike
    }

    private fun updateCondition(condition: String) {
        currentTextCondition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: String) {
        currentPrecipitation.text = precipitationVolume
    }

    private fun updateWind(windText: String) {
        currentWind.text = windText
    }

    private fun updateVisibility(visibilityText: String) {
        currentVisibility.text = visibilityText
    }

    private suspend fun refreshWeather() {
        viewModel.requestRefreshOfData()
    }
}
