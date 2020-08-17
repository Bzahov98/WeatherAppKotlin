package com.bzahov.weatherapp.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import com.bzahov.weatherapp.ForecastApplication
import com.bzahov.weatherapp.ForecastApplication.Companion.getAppString
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.provider.UNIT_SYSTEM
import com.bzahov.weatherapp.ui.remoteviews.widgets.CurrentWeatherWidgetConfigureActivity
import com.bzahov.weatherapp.ui.base.ScopedPreferenceCompatFragment
import com.bzahov.weatherapp.ui.base.fragments.SettingsFragmentViewModel
import com.bzahov.weatherapp.ui.base.fragments.SettingsFragmentViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "SettingsFragment"


class SettingsFragment() : ScopedPreferenceCompatFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener, KodeinAware {


    override val kodein by closestKodein()
    private val viewModelFactory: SettingsFragmentViewModelFactory by instance<SettingsFragmentViewModelFactory>()
    private lateinit var viewModel: SettingsFragmentViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Settings"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = null
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(SettingsFragmentViewModel::class.java)

        makeSelectablePreferences()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val key = preference?.key ?: return super.onPreferenceTreeClick(preference)

        return when (key) {
            UNIT_SYSTEM -> {
                true
            }
            SHOW_NOTIFICATIONS -> {

                Toast.makeText(context,"bla", Toast.LENGTH_SHORT).show()
//
//                val notificationManager = ContextCompat.getSystemService(
//                    requireContext(),
//                    NotificationManager::class.java
//                ) as NotificationManager
//
//                notificationManager.sendNotification(
//                    requireContext().getText(R.string.notification_ready).toString(),
//                    requireContext()
//                )

                (activity?.application as ForecastApplication).testAlarmNotification()
                return true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    override fun onResume() {
        super.onResume()
        makeSelectablePreferences()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    // TODO: check is there connection and forbid changing UnitSystem when there is no internet
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            UNIT_SYSTEM -> {
                makeSelectablePreferences()
                //if(viewModel.isMetric && sharedPreferences.get)
                Log.d(TAG, "notifyForUnitSystemChanged")
                //viewModel.notifyForUnitSystemChanged()
                launch {
                    viewModel.requestRefreshOfData();
                }
            }
            CUSTOM_LOCATION, USE_DEVICE_LOCATION -> {
                launch {
                    viewModel.requestRefreshOfData();
                }
            }
            ENABLE_WEATHER_WIDGET -> {
                val widgetPreference =
                    preferenceManager.findPreference<SwitchPreference>(ENABLE_WEATHER_WIDGET)
                val widgetRefreshRatePreference =
                    preferenceManager.findPreference<SeekBarPreference>(WEATHER_WIDGET_REFRESH_RATE)

                if (widgetPreference != null) {
                    if (widgetPreference.isChecked) {
                        Log.d(TAG, "enable weather widget")
                        val intent = Intent(context, CurrentWeatherWidgetConfigureActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            WEATHER_WIDGET_REFRESH_RATE -> {
                Log.d(TAG, "widgetRefresh rate")
            }
        }
    }

    private fun makeSelectablePreferences() {
        val unitSystemPreference = preferenceManager.findPreference<ListPreference>(UNIT_SYSTEM)
        unitSystemPreference?.isSelectable = viewModel.isOnline()
        val currentLocationPreference =
            preferenceManager.findPreference<SwitchPreference>(USE_DEVICE_LOCATION)

        val isOnlineAndHaveLocation = viewModel.isOnline() && viewModel.isLocationEnabled()

        currentLocationPreference?.isSelectable = isOnlineAndHaveLocation
        if (!isOnlineAndHaveLocation) {
            Toast.makeText(
                context,
                "Please make sure you are online and have location on",
                Toast.LENGTH_LONG
            ).show()
        }
        val locationPreference: EditTextPreference? =
            preferenceManager.findPreference<EditTextPreference>(CUSTOM_LOCATION)

        if (!viewModel.isLocationEnabled() && viewModel.isOnline()) {
            locationPreference?.isSelectable = true
        } else {
            locationPreference?.isSelectable = viewModel.isOnline()
        }
    }
    companion object{

        val ENABLE_WEATHER_WIDGET = getAppString(R.string.preferences_enable_weather_widget)
        val WEATHER_WIDGET_REFRESH_RATE = getAppString(R.string.preferences_widget_refresh_rate)
        val SHOW_NOTIFICATIONS = getAppString(R.string.preferences_show_notifications)

        val CUSTOM_LOCATION = getAppString(R.string.preferences_location_custom)
        val USE_DEVICE_LOCATION = getAppString(R.string.preferences_location_use_device)
        val UNIT_SYSTEM = getAppString(R.string.preferences_unit_system)
    }
}