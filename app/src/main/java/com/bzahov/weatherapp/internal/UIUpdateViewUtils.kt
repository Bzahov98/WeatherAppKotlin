package com.bzahov.weatherapp.internal

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.db.entity.forecast.model.Wind
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.chooseLocalizedUnitAbbreviation
import com.bzahov.weatherapp.internal.UIConverterFieldUtils.Companion.convertDoubleToWindDirectionString
import com.bzahov.weatherapp.internal.glide.GlideApp
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

@SuppressLint("SetTextI18n")
class UIUpdateViewUtils {
    companion object {
        fun updateWind(wind: Wind, isMetric: Boolean, windTextView: TextView) {

            windTextView.text =
                calculateWindDirectionToString(wind, isMetric)
        }

        fun updateWindShort(wind: Wind, isMetric: Boolean, windTextView: TextView) {

            windTextView.text =
                calculateWindDirectionToString(wind, isMetric, true)
        }

        fun calculateWindDirectionToString(
            wind: Wind, isMetric: Boolean, isShortVersion: Boolean = false
        ): String {
            val unitAbbreviation = chooseLocalizedUnitAbbreviation(
                isMetric,
                getAppString(R.string.metric_speed),
                getAppString(R.string.imperial_speed)
            )

            var convertWindDirectionToString = ""
            var windString = ""
            if (!isShortVersion) {
                windString = getAppString(R.string.weather_text_wind) + " "
                convertWindDirectionToString = convertDoubleToWindDirectionString(
                    wind.deg
                ) + ", "
            }
            return windString +
                    "$convertWindDirectionToString${wind.speed} $unitAbbreviation"
        }

        fun updateActionBarTitle(location: String, activity: FragmentActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = location
        }

        fun updateActionBarSubtitleWithResource(resourceInt: Int, activity: FragmentActivity) {
            updateActionBarSubtitle(getAppString(resourceInt), activity)
        }

        fun updateActionBarSubtitle(text: String, activity: FragmentActivity) {
            (activity as AppCompatActivity).supportActionBar?.subtitle = text
        }

        fun updateIcon(iconNumber: String, iconView: ImageView) {
            val iconUrl = UIConverterFieldUtils.getOpenWeatherIconUrl(iconNumber)
            GlideApp.with(iconView.rootView)
                .load(iconUrl)
                .into(iconView)
        }
        fun showSnackBarMessage(message: String, activity: FragmentActivity?, isSuccessful: Boolean = true) {
            val bottomNavView: BottomNavigationView = activity?.findViewById(R.id.bottom_navigation)!!
            val snackbar = Snackbar.make(bottomNavView, message, Snackbar.LENGTH_LONG).apply {
                anchorView = bottomNavView
            }
            if (!isSuccessful) {
                snackbar.setAction("Open Network Panel") {
                    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    activity.startActivity(intent)
                }
            } else {
                snackbar.setAction("Dismiss") { snackbar.dismiss() }
            }
            snackbar.show()
        }
    }
}