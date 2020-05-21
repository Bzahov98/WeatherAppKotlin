package com.bzahov.weatherapp.ui.weather.future.detail

import android.annotation.SuppressLint
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
import com.bzahov.weatherapp.data.db.LocalDateConverter
import com.bzahov.weatherapp.data.db.entity.forecast.entities.FutureDayData
import com.bzahov.weatherapp.data.db.entity.forecast.entities.WeatherDetails
import com.bzahov.weatherapp.internal.UIConverterFieldUtils
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.dateTimestampToDateTimeString
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateActionBarSubtitle
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateIcon
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateLocation
import com.bzahov.weatherapp.internal.UIUpdateViewUtils.Companion.updateWind
import com.bzahov.weatherapp.internal.exceptions.DateNotFoundException
import com.bzahov.weatherapp.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.future_detail_weather_fragment.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.time.LocalDateTime
@SuppressLint("SetTextI18n")
class FutureDetailWeatherFragment : ScopedFragment(), KodeinAware {
    private val TAG = "FutureDetailWeatherFragment"
    override val kodein by closestKodein()

    private val viewModelFactoryInstanceFactory: ((LocalDateTime) -> FutureDetailWeatherViewModelFactory)
            by factory<LocalDateTime, FutureDetailWeatherViewModelFactory>()

    private lateinit var viewModel: FutureDetailWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.future_detail_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val safeArgs = arguments?.let { FutureDetailWeatherFragmentArgs.fromBundle(it) }
        val date = LocalDateConverter().stringToDateTime(safeArgs?.dateString)
            ?: throw DateNotFoundException()

        Log.e(TAG,"received ${safeArgs?.dateString}safeargs: $date")

        viewModel = ViewModelProvider(this, viewModelFactoryInstanceFactory(date))
            .get(FutureDetailWeatherViewModel::class.java)
//
        bindUI()
    }

    private fun bindUI(): Job {
        return launch {
            val futureDetailWeatherLiveData = viewModel.weather.await()
            val weatherLocation = viewModel.weatherLocation.await()
            // REWORK Return City object temporary FiX for: futureDetail location isn't changed and stay with name futureDetail_weather_fragment all city attributes are null

            weatherLocation.observe(viewLifecycleOwner, Observer { location ->
                if (location == null) return@Observer
                Log.e(TAG, "Location : $location")
                updateLocation(location.name, requireActivity())
                Log.d(TAG, "Update location with that data: $location")
            })

            futureDetailWeatherLiveData.observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "UpdateUI for FutureDayData with: \n ${it ?: "null"} \n")
                if (it == null) return@Observer
                updateUI(it)
            })
        }
    }

    private fun updateUI(it: FutureDayData) {
        Log.d(TAG, "UpdateUI for FutureDayData with: \n $it \n")
        futureDetailGroupLoading.visibility = View.GONE
        updateCondition(it.weatherDetails)
        updateActionBarSubtitle(
            dateTimestampToDateTimeString(it.dt),
            (activity as AppCompatActivity)
        )
        updatePrecipitation(it)
        updateTemperatures(it.main.temp, it.main.feelsLike)
        updateWind(it.wind.deg, it.wind.speed, viewModel.isMetric, futureDetailWind)
        updateVisibility(it.clouds.all)
        updateBackground(it)
        updateIcon(it.weatherDetails.last().icon, futureDetailIConditionIcon)
    }

    // Rework change background with proper color to match to all pictures background
    private fun updateBackground(it: FutureDayData) {
        if (it.sys.pod == getString(R.string.weather_open_is_day)) {
            futureDetailWeatherFragment.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorWeatherIconBackgroundDay
                )
            )
        } else {
            futureDetailWeatherFragment.setBackgroundColor(
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
        futureDetailTextTemperature.text = "$temp$unitAbbreviation"
        futureDetailFeelsLikeTemperature.text =
            getString(R.string.feels_like).plus("$feelsLike$unitAbbreviation")
    }

    private fun updateCondition(weatherDescriptionList: List<WeatherDetails>) {
        futureDetailTextCondition.text =
            UIConverterFieldUtils.getAllDescriptionsString(weatherDescriptionList)
    }

    private fun updatePrecipitation(dayInfo: FutureDayData) {
        // always in mm
        val unitAbbreviation = getString(R.string.metric_precipitation)
        val snowVolume3h = dayInfo.snow?.precipitationsForLast3hours
        val rainVolume3h = dayInfo.rain?.precipitationsForLast3hours

        if (snowVolume3h ?: 0.0 > 0 && rainVolume3h ?: 0.0 > 0) {
            futureDetailRainPrecipitation.text =
                getString(R.string.weather_text_rain) +
                        " $rainVolume3h $unitAbbreviation, " +
                        getString(
                            R.string.weather_text_snow
                        ) +
                        ": $snowVolume3h"
            return
        }
        if (snowVolume3h ?: 0.0 > 0) {
            futureDetailRainPrecipitation.text =
                getString(R.string.weather_text_precipitation_snow) + " $snowVolume3h $unitAbbreviation"
        }
        if (rainVolume3h ?: 0.0 > 0) {
            futureDetailRainPrecipitation.text =
                getString(R.string.weather_text_precipitation_rain) + "$rainVolume3h $unitAbbreviation"
        }

    }

    private fun updateVisibility(visibilityDistance: Int) {
        val unitAbbreviation = getString(R.string.percentage)
        futureDetailVisibility.text =
            getString(R.string.weather_text_cloudiness) + " $visibilityDistance $unitAbbreviation"
    }

    private suspend fun refreshWeather() {
        viewModel.requestRefreshOfData()
    }
}
