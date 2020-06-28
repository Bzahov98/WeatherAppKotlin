package com.bzahov.weatherapp.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.bzahov.weatherapp.R
import com.bzahov.weatherapp.data.provider.CUSTOM_LOCATION
import com.bzahov.weatherapp.data.provider.UNIT_SYSTEM
import com.bzahov.weatherapp.data.provider.USE_DEVICE_LOCATION
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
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Setting"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = null
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(SettingsFragmentViewModel::class.java)

        makeSelectablePreferences()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val key = preference?.key ?: return super.onPreferenceTreeClick(preference)

        return if (key == UNIT_SYSTEM) {
            true
        } else super.onPreferenceTreeClick(preference)
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
        if (key == UNIT_SYSTEM) {
            makeSelectablePreferences()
            //if(viewModel.isMetric && sharedPreferences.get)
            Log.d(TAG, "notifyForUnitSystemChanged")
            //viewModel.notifyForUnitSystemChanged()
            launch {
                viewModel.requestRefreshOfData();
            }
        }
        if (key == CUSTOM_LOCATION || key == USE_DEVICE_LOCATION) {
            launch {
                viewModel.requestRefreshOfData();
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
        val locationPreference: EditTextPreference? = preferenceManager.findPreference<EditTextPreference>(CUSTOM_LOCATION)

        if (!viewModel.isLocationEnabled() && viewModel.isOnline()) {
            locationPreference?.isSelectable = true
        } else {
            locationPreference?.isSelectable = viewModel.isOnline()
        }
    }
}